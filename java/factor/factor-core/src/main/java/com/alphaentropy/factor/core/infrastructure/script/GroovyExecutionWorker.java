package com.alphaentropy.factor.core.infrastructure.script;

import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GroovyExecutionWorker implements Callable<Object> {

    private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?(E-?\\D+)?");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern NUMBER_PATTERN_INCOMPLETE = Pattern.compile("-?(\\.\\d+)?");

    private String script;
    private Map<String, String> instrumentId;
    private Set<Map<String, String>> failedIds;
    private Map<String, Object> instrumentContext;
    private GroovyScriptCache scriptCache;

    @Override
    public Object call() throws Exception {
        Script shell = scriptCache.get(script);
        Binding binding = getBinding(instrumentContext);
        shell.setBinding(binding);
        try {
            Object res = shell.run();
            instrumentContext.putAll(binding.getVariables());
            return res;
        } catch (Exception e) {
            log.warn(e.getMessage() + " while running " + script + " for " + instrumentId);
            failedIds.add(instrumentId);
        } finally {
            scriptCache.returnScript(script, shell);
        }
        return null;
    }

    private Binding getBinding(Map<String, Object> map) {
        Binding binding = new Binding();
        for (String key : map.keySet()) {
            binding.setVariable(key, convertGroovyVariable(map.get(key)));
        }
        return binding;
    }

    private Object convertGroovyVariable(Object value) {
        Object ret = null;
        if (value instanceof String) {
            String strVal = (String) value;
            if (isNumeric(strVal) || isScientificNotation(strVal)) {
                ret = new FieldSingularValue(new BigDecimal(strVal));
            } else if (!strVal.isEmpty() && isIncompleteNumeric(strVal)) {
                ret = new FieldSingularValue(new BigDecimal(Double.parseDouble(strVal)));
            } else {
                ret = strVal;
            }
        } else if (value instanceof Integer) {
            ret = new FieldSingularValue(new BigDecimal((Integer) value));
        } else if (value instanceof BigDecimal) {
            ret = new FieldSingularValue(value);
        } else if (value instanceof List) {
            List list = (List) value;
            ret = toFieldList(list);
        }
        return ret;
    }

    private Object toFieldList(List list) {
        if (list.isEmpty()) {
            return new FieldListValue(list);
        }
        Object ret = list;
        Object first = list.get(0);
        if (first instanceof Pair) {
            List left = new ArrayList();
            List right = new ArrayList();
            list.forEach(p -> {
                Pair pair = (Pair) p;
                left.add(new FieldSingularValue(new BigDecimal(pair.getFirst().toString())));
                right.add(new FieldSingularValue(new BigDecimal(pair.getSecond().toString())));
            });
            ret = new FieldPairValue(new FieldListValue(left), new FieldListValue(right));
        } else if (first instanceof String) {
            ret = new FieldListValue(list.stream().map(e -> new FieldSingularValue(new BigDecimal(e.toString()))).collect(Collectors.toList()));
        } else if (first instanceof FieldSingularValue) {
            ret = new FieldListValue(list);
        } else if (first instanceof BigDecimal) {
            ret = new FieldListValue(list.stream().map(e -> new FieldSingularValue(e)).collect(Collectors.toList()));
        }
        return ret;
    }

    private boolean isIncompleteNumeric(String strVal) {
        if (strVal == null) {
            return false;
        }
        return NUMBER_PATTERN_INCOMPLETE.matcher(strVal).matches();
    }

    private boolean isScientificNotation(String strVal) {
        if (strVal == null) {
            return false;
        }
        return SCIENTIFIC_NOTATION_PATTERN.matcher(strVal).matches();
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return NUMBER_PATTERN.matcher(strNum).matches();
    }
}
