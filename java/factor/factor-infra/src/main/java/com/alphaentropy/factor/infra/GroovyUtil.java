package com.alphaentropy.factor.infra;

import org.apache.commons.math3.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GroovyUtil {

    private static final Pattern SCIENTIFIC_NOTATION_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?(E-?\\D+)?");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern NUMBER_PATTERN_INCOMPLETE = Pattern.compile("-?(\\.\\d+)?");

    public static Object convertGroovyVariable(Object value) {
        Object ret = value;
        if (value instanceof String) {
            String strVal = (String) value;
            if (isNumeric(strVal) || isScientificNotation(strVal)) {
                ret = new SingularValue(new BigDecimal(strVal));
            } else if (!strVal.isEmpty() && isIncompleteNumeric(strVal)) {
                ret = new SingularValue(new BigDecimal(Double.parseDouble(strVal)));
            } else {
                ret = strVal;
            }
        } else if (value instanceof Integer) {
            ret = new SingularValue(new BigDecimal((Integer) value));
        } else if (value instanceof BigDecimal) {
            ret = new SingularValue(value);
        } else if (value instanceof Collection) {
            Collection list = (Collection) value;
            ret = toFieldList(list);
        } else if (value instanceof Pair) {
            Pair pair = (Pair) value;
            ret = new SingularPairValue(new SingularValue(pair.getFirst()), new SingularValue(pair.getSecond()));
        }
        return ret;
    }

    private static Object toFieldList(Collection list) {
        if (list.isEmpty()) {
            return new ListValue(list);
        }
        Object ret = list;
        Object first = getFirst(list);
        if (first instanceof Pair) {
            List left = new ArrayList();
            List right = new ArrayList();
            if (left.size() == right.size()) {
                list.forEach(p -> {
                    Pair pair = (Pair) p;
                    left.add(new SingularValue(new BigDecimal(pair.getFirst().toString())));
                    right.add(new SingularValue(new BigDecimal(pair.getSecond().toString())));
                });
                ret = new PairValue(new ListValue(left), new ListValue(right));
            }
        } else if (first instanceof String) {
            ret = new ListValue(list.stream().map(e -> new SingularValue(new BigDecimal(e.toString()))).collect(Collectors.toList()));
        } else if (first instanceof SingularValue) {
            ret = new ListValue(list);
        } else if (first instanceof BigDecimal) {
            ret = new ListValue(list.stream().map(e -> new SingularValue(e)).collect(Collectors.toList()));
        }
        return ret;
    }

    private static Object getFirst(Collection list) {
        return list.iterator().next();
    }

    private static boolean isIncompleteNumeric(String strVal) {
        if (strVal == null) {
            return false;
        }
        return NUMBER_PATTERN_INCOMPLETE.matcher(strVal).matches();
    }

    private static boolean isScientificNotation(String strVal) {
        if (strVal == null) {
            return false;
        }
        return SCIENTIFIC_NOTATION_PATTERN.matcher(strVal).matches();
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return NUMBER_PATTERN.matcher(strNum).matches();
    }
}
