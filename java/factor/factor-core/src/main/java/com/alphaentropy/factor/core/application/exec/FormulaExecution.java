package com.alphaentropy.factor.core.application.exec;

import com.alphaentropy.factor.core.domain.*;
import com.alphaentropy.factor.core.infrastructure.builtin.BuiltinFunction;
import com.alphaentropy.factor.core.infrastructure.builtin.BuiltinFunctionFactory;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import com.alphaentropy.factor.core.infrastructure.script.ScriptExecutor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class FormulaExecution {
    private static final String INDEX_REFERENCE = "$index.";
    private BuiltinFunctionFactory functionFactory;
    private ScriptExecutor scriptExecutor;
    private FieldAccessor fieldAccessor;
    // key map - instrument id field and values; value map - variable name and value
    private Map<Map<String, String>, Map<String, Object>> perInstrumentBindings;
    private Collection<Map<String, String>> allInstrumentIds;
    private FormulaCall call;
    private CompiledFormula compiledFormula;
    private CalculationContext context;

    public FormulaExecution(BuiltinFunctionFactory functionFactory, ScriptExecutor scriptExecutor,
                            FieldAccessor fieldAccessor, FormulaCall call, CompiledFormula compiledFormula, CalculationContext context) {
        this.functionFactory = functionFactory;
        this.scriptExecutor = scriptExecutor;
        this.fieldAccessor = fieldAccessor;
        this.call = call;
        this.compiledFormula = compiledFormula;
        this.context = context;
        this.perInstrumentBindings = new HashMap<>();
        initializeBindings();
    }

    private void initializeBindings() {
        for (Map<String, String> instrumentId : context.getInstrumentIndexIdMappings().keySet()) {
            this.perInstrumentBindings.put(instrumentId, new HashMap<String, Object>() {{
                // inject formula parameters into bindings
                putAll(call.getParameters());
            }});
        }
        this.allInstrumentIds = new ArrayList<>(perInstrumentBindings.keySet());
    }

    public Map<Map<String, String>, Map<String, String>> execute() {
        for (NormalizedScriptLine line : compiledFormula.getNormalizedScripts()) {
            Map<String, BuiltinFunctionCall> biFuncMap = line.getBuiltin();
            executeBuiltinFunctions(biFuncMap);
            Set<Map<String, String>> failedIds = scriptExecutor.execute(line.getSource(), perInstrumentBindings);
            // remove the failed ids from ids to be calculated
            failedIds.forEach(id -> perInstrumentBindings.remove(id));
        }
        return collectResult();
    }

    private Map<Map<String, String>, Map<String, String>> collectResult() {
        Map<Map<String, String>, Map<String, String>> ret = new HashMap<>();
        List<FormulaOutput> outputFieldToVariableMap = call.getOutputMapping();
        List<FormulaExpression> defaultExpList = outputFieldToVariableMap.stream()
                .map(f -> compileExp(f.getDefaultValue(), f.getFieldName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Map<String, String>, Map<String, String>> defaultValues = loadDefaultValues(defaultExpList);
        List<FormulaExpression> initialExpList = outputFieldToVariableMap.stream()
                .map(f -> compileExp(f.getInitialValue(), f.getFieldName()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Map<String, String>, Map<String, String>> initialValues = loadDefaultValues(initialExpList);
        for (Map<String, String> instrumentId : allInstrumentIds) {
            Map<String, Object> instrumentContext = perInstrumentBindings.get(instrumentId);
            Map<String, String> instrumentResult = new HashMap<>();
            outputFieldToVariableMap.forEach(o -> {
                String variable = o.getVariableName();
                String field = o.getFieldName();
                if (instrumentContext != null && instrumentContext.containsKey(variable)) {
                    instrumentResult.put(field, round(instrumentContext.get(variable).toString()));
                } else {
                    if (o.getInitialValue() != null && !fieldAccessor.exists(instrumentId, field)) {
                        instrumentResult.put(field, initialValues.get(instrumentId).get(field));
                    } else if (o.getDefaultValue() != null) {
                        instrumentResult.put(field, defaultValues.get(instrumentId).get(field));
                    }
                }
            });
            if (!instrumentResult.isEmpty()) {
                ret.put(instrumentId, instrumentResult);
            }
        }
        return ret;
    }

    private String round(String strVal) {
        try {
            return new BigDecimal(strVal).setScale(call.getScale(), RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            log.warn("Invalid number to round {}", strVal);
            return strVal;
        }
    }

    private FormulaExpression compileExp(String exp, String fieldName) {
        if (exp != null) {
            FormulaExpression.Target target = FormulaExpression.Target.instrument;
            FormulaExpression.Type type = FormulaExpression.Type.constant;
            String variableName = exp;
            if (exp.contains(".")) {
                if (exp.startsWith(INDEX_REFERENCE)) {
                    target = FormulaExpression.Target.index;
                }
                variableName = exp.split("\\.", 2)[1];
            }
            if (variableName.startsWith("$$")) {
                type = FormulaExpression.Type.DB;
                variableName = variableName.substring(2);
            } else if (variableName.startsWith("$")) {
                type = FormulaExpression.Type.context;
                variableName = variableName.substring(1);
            }
            return FormulaExpression.builder()
                    .fieldName(fieldName)
                    .variableName(variableName)
                    .target(target)
                    .type(type)
                    .build();
        }
        return null;
    }

    private Map<Map<String, String>, Map<String, String>> loadDefaultValues(List<FormulaExpression> expList) {
        Map<Map<String, String>, Map<String, String>> res = new HashMap<>();
        for (Map<String, String> ins : allInstrumentIds) {
            res.put(ins, new HashMap<>());
        }
        for (FormulaExpression dv : expList) {
            if (dv == null) {
                continue;
            }
            Set<Map<String, String>> toQuery;
            if (dv.getTarget() == FormulaExpression.Target.index) {
                toQuery = new HashSet<>();
                for (Map<String, String> id : allInstrumentIds) {
                    toQuery.add(context.getInstrumentIndexIdMappings().get(id));
                }
            } else {
                toQuery = new HashSet<>(allInstrumentIds);
            }
            Map<Map<String, String>, String> values = new HashMap<>();
            switch (dv.getType()) {
                case constant:
                    for (Map<String, String> ins : res.keySet()) {
                        values.put(ins, dv.getVariableName());
                    }
                    break;
                case context:
                    values = toQuery.stream().collect(Collectors.toMap(
                            key -> key,
                            key -> Optional.ofNullable(perInstrumentBindings.get(key))
                                    .map(k -> k.get(dv.getVariableName()).toString())
                                    .orElse("")
                    ));
                    break;
                case DB:
                    values = fieldAccessor.get(dv.getVariableName(), toQuery, context.getDate(), true);
            }
            if (dv.getTarget() == FormulaExpression.Target.index) {
                for (Map<String, String> key : res.keySet()) {
                    res.get(key).put(dv.getFieldName(), values.get(context.getInstrumentIndexIdMappings().get(key)));
                }
            } else {
                for (Map<String, String> key : res.keySet()) {
                    res.get(key).put(dv.getFieldName(), values.get(key));
                }
            }
        }
        return res;
    }

    private void executeBuiltinFunctions(Map<String, BuiltinFunctionCall> biFuncMap) {
        for (String variable : biFuncMap.keySet()) {
            BuiltinFunctionCall functionCall = biFuncMap.get(variable);
            BuiltinFunction biFunc = functionFactory.getBuiltinFunction(functionCall.getFunctionName());
            Map<Map<String, String>, List<String>> resolvedParameters = resolveVariableInBiFuncParams(functionCall.getParameters());
            if (!noInstrumentsToCalc(resolvedParameters)) {
                Map<Map<String, String>, Object> funcRet = biFunc.invoke(fieldAccessor, context, resolvedParameters);
                setFuncResultToBinding(variable, funcRet);
            }
        }
    }

    private void setFuncResultToBinding(String variable, Map<Map<String, String>, Object> funcRet) {
        for (Map<String, String> instrumentId : funcRet.keySet()) {
            if (perInstrumentBindings.containsKey(instrumentId)) {
                this.perInstrumentBindings.get(instrumentId).put(variable, funcRet.get(instrumentId));
            }
        }
    }

    private Map<Map<String, String>, List<String>> resolveVariableInBiFuncParams(List<String> parameters) {
        Map<Map<String, String>, List<String>> ret = new HashMap<>();
        for (Map<String, String> instrumentId : perInstrumentBindings.keySet()) {
            Map<String, Object> instrumentContext = perInstrumentBindings.get(instrumentId);
            List<String> resolvedParams = new ArrayList<>();
            for (String param : parameters) {
                if (instrumentContext.containsKey(param)) {
                    String variableValue = instrumentContext.get(param).toString();
                    resolvedParams.add(variableValue);
                } else {
                    resolvedParams.add(param);
                }
            }
            ret.put(instrumentId, resolvedParams);
        }
        return ret;
    }

    private boolean noInstrumentsToCalc(Map<Map<String, String>, List<String>> resolvedParameters) {
        return perInstrumentBindings.isEmpty() && resolvedParameters.isEmpty();
    }
}
