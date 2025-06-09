package com.alphaentropy.factor.core.application.compile;

import com.alphaentropy.factor.core.application.exception.IllegalBuiltinFunctionException;
import com.alphaentropy.factor.core.domain.BuiltinFunctionCall;
import com.alphaentropy.factor.core.domain.CompiledFormula;
import com.alphaentropy.factor.core.domain.Formula;
import com.alphaentropy.factor.core.domain.NormalizedScriptLine;
import com.alphaentropy.factor.core.infrastructure.builtin.BuiltinFunctionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alphaentropy.factor.core.infrastructure.builtin.BuiltinFunctionFactory.GET_FIELD;

@Lazy
@Component
public class FormulaCompiler {
    private static final String INDEX = "index";
    private static final String THIS = "this";

    @Autowired
    private BuiltinFunctionFactory functionFactory;

    public CompiledFormula compile(Formula formula) {
        Set<String> referenceFields = new HashSet<>();
        List<NormalizedScriptLine> normalizedScripts = new ArrayList<>();
        groupRawLines(formula.getRawLines()).forEach(line -> normalizedScripts.add(normalizeLine(line, referenceFields)));
        CompiledFormula compiledFormula = new CompiledFormula(formula, referenceFields, normalizedScripts);
        validateCompiledFormula(compiledFormula);
        return compiledFormula;
    }

    private List<String> groupRawLines(List<String> rawLines) {
        List<String> ret = new ArrayList<>();
        Pattern eachStart = Pattern.compile(".*.(each|loop) *\\{.*}");
        boolean hasBlock = false;
        StringBuilder sb = null;
        for (String rawLine : rawLines) {
            if (eachStart.matcher(rawLine).matches()) {
                hasBlock = true;
                sb = new StringBuilder();
            }
            if (hasBlock) {
                sb.append(rawLine);
                if (rawLine.trim().startsWith("}")) {
                    hasBlock = false;
                    ret.add(sb.toString());
                }
            } else {
                ret.add(rawLine);
            }
        }
        return ret;
    }

    private NormalizedScriptLine normalizeLine(String rawline, Set<String> referenceFields) {
        Map<String, BuiltinFunctionCall> biFuncCallMap = new HashMap<>();
        String source = recognizeFieldAccess(rawline, referenceFields, biFuncCallMap);
        source = recognizeBuiltinFunction(source, referenceFields, biFuncCallMap);
        return new NormalizedScriptLine(source, biFuncCallMap);
    }

    private String recognizeFieldAccess(String rawline, Set<String> referenceFields, Map<String, BuiltinFunctionCall> biFuncCallMap) {
        String normalized = rawline;
        Pattern fieldPattern = Pattern.compile("\\$((index.)*)(\\w+)");
        Pattern fieldParamPattern = Pattern.compile("\\$((index.)*)(\\w+) *\\([^\\)]*\\)");
        Matcher m = fieldParamPattern.matcher(rawline);
        while (m.find()) {
            String fieldParam = m.group(0);
            BuiltinFunctionCall biFuncCall = convertFieldAccessToBiFunc(fieldParam, referenceFields);
            String variable = convertFieldAccessToVariable(fieldParam);
            normalized = normalized.replace(fieldParam, variable);
            biFuncCallMap.put(variable, biFuncCall);
        }
        m = fieldPattern.matcher(normalized);
        String postNormalized = normalized;
        while (m.find()) {
            String field = m.group(0);
            if (!normalized.substring(m.end()).trim().startsWith("(")) {
                BuiltinFunctionCall biFuncCall = convertFieldAccessToBiFunc(field, referenceFields);
                String variable = convertFieldAccessToVariable(field);
                postNormalized = postNormalized.replace(field, variable);
                biFuncCallMap.put(variable, biFuncCall);
            }
        }
        return postNormalized;
    }

    private String convertFieldAccessToVariable(String fieldParam) {
        return fieldParam.replace("(", "_").replace(".", "_")
                .replace("-", "_").replace(")", "")
                .replace(",", "_").replace(" ", "")
                .replace("$", "").toLowerCase();
    }

    private String convertBiFuncToVariable(String biFunc) {
        return biFunc.replace("(", "_").replace(".", "_")
                .replace("-", "_").replace(")", "")
                .replace(",", "_").replace(" ", "")
                .toLowerCase();
    }

    private BuiltinFunctionCall convertFieldAccessToBiFunc(String fieldParam, Set<String> referenceFields) {
        List<String> params = new ArrayList<>();
        if (!fieldParam.contains("(")) {
            String fieldName = fieldParam.substring(1);
            extractFieldName(fieldName, params, referenceFields);
            return new BuiltinFunctionCall(GET_FIELD, params);
        }
        String paramStr = fieldParam.substring(fieldParam.indexOf("(") + 1, fieldParam.indexOf(")"));
        String[] tokens = paramStr.split(",");
        String fieldName = fieldParam.substring(1, fieldParam.indexOf("("));
        extractFieldName(fieldName, params, referenceFields);
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                params.add(token.trim());
            }
        }
        validateBuiltinFunction(GET_FIELD, params);
        return new BuiltinFunctionCall(GET_FIELD, params);
    }

    private void validateBuiltinFunction(String funcName, List<String> params) {
        int paramHighLimit = functionFactory.getBuiltinFunction(funcName).maxParameters();
        int paramLowLimit = functionFactory.getBuiltinFunction(funcName).minParameters();
        int paramSize = params.size();
        if (paramSize > paramHighLimit || paramSize < paramLowLimit) {
            throw new IllegalBuiltinFunctionException(funcName + " receive " + paramSize + " parameters, exceed limit: "
                    + paramLowLimit + " ~ " + paramHighLimit);
        }
    }

    private void extractFieldName(String fieldName, List<String> params, Set<String> referenceFields) {
        if (fieldName.startsWith(INDEX + ".")) {
            params.add(INDEX);
            fieldName = fieldName.substring(fieldName.indexOf(".") + 1);
        } else {
            params.add(THIS);
        }
        params.add(fieldName);
        referenceFields.add(fieldName);
    }

    private String recognizeBuiltinFunction(String rawLine, Set<String> referenceFields, Map<String, BuiltinFunctionCall> biFuncCallMap) {
        String normalized = rawLine;
        Pattern biFuncPattern = getBuiltinFuncPattern();
        Matcher m = biFuncPattern.matcher(rawLine);
        while (m.find()) {
            String biFunc = m.group(0);
            BuiltinFunctionCall biFuncCall = getBiFunc(biFunc);
            recognizeReferenceFieldsFromBuiltinFunc(biFuncCall, referenceFields);
            String variable = convertBiFuncToVariable(biFunc);
            normalized = normalized.replace(biFunc, variable);
            biFuncCallMap.put(variable, biFuncCall);
        }
        return normalized;
    }

    private void recognizeReferenceFieldsFromBuiltinFunc(BuiltinFunctionCall biFuncCall, Set<String> referenceFields) {
        int[] fieldsIndices = functionFactory.getBuiltinFunction(biFuncCall.getFunctionName()).referenceFieldsIndices();
        if (fieldsIndices.length > 0) {
            for (int fieldIdx : fieldsIndices) {
                if (fieldIdx < biFuncCall.getParameters().size()) {
                    referenceFields.add(biFuncCall.getParameters().get(fieldIdx).replace("index.", ""));
                }
            }
        }
    }

    private BuiltinFunctionCall getBiFunc(String biFunc) {
        List<String> params = new ArrayList<>();
        if (!biFunc.contains("(")) {
            return new BuiltinFunctionCall(biFunc, params);
        }
        String paramStr = biFunc.substring(biFunc.indexOf("(") + 1, biFunc.indexOf(")"));
        String[] tokens = paramStr.split(",");
        String funcName = biFunc.substring(0, biFunc.indexOf("("));
        for (String token : tokens) {
            if (!token.trim().isEmpty()) {
                params.add(token.trim());
            }
        }
        validateBuiltinFunction(funcName, params);
        return new BuiltinFunctionCall(funcName, params);
    }

    private Pattern getBuiltinFuncPattern() {
        Set<String> allFuncs = functionFactory.getAllFunctions();
        return Pattern.compile("(" + String.join("|", allFuncs) + ") *\\([^\\)]*\\)");
    }

    private void validateCompiledFormula(CompiledFormula compiledFormula) {
        //ignore for now
    }


}
