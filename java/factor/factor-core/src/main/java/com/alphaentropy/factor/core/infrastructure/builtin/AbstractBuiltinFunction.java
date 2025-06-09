package com.alphaentropy.factor.core.infrastructure.builtin;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public abstract class AbstractBuiltinFunction implements BuiltinFunction {
    private static final String MONTH = "M";
    private static final String YEAR = "Y";
    private static final String DT_FMT = "yyyyMMdd";

    abstract int[] getStaticParamsIndices();

    abstract int getDynamicParamsIndex();

    Map<Integer, String> getStaticParams(Map<Map<String, String>, List<String>> params) {
        Map<Integer, String> ret = new HashMap<>();
        if (!params.isEmpty()) {
            int[] staticParamsIndices = getStaticParamsIndices();
            List<String> first = params.values().iterator().next();
            for (int idx : staticParamsIndices) {
                if (idx < first.size()) {
                    ret.put(idx, first.get(idx));
                }
            }
        }
        return ret;
    }

    Map<String, Collection<Map<String, String>>> getGroupedDynamicParams(Map<Map<String, String>, List<String>> params) {
        Map<String, Collection<Map<String, String>>> ret = new HashMap<>();
        int idx = getDynamicParamsIndex();
        params.forEach((id, list) -> {
            if (idx < list.size()) {
                String paramVal = list.get(idx);
                Collection<Map<String, String>> ids = ret.computeIfAbsent(paramVal, k -> new HashSet<>());
                ids.add(id);
            }
        });
        return ret;
    }

    String resolveStartDate(String pivotDate, String pastPeriod) {
        SimpleDateFormat format = new SimpleDateFormat(DT_FMT);
        Calendar c = Calendar.getInstance();
        try {
            Date td = format.parse(pivotDate);
            int numPeriod = Integer.parseInt(pastPeriod.substring(0, pastPeriod.length() - 1));
            c.setTime(td);
            int field = -1;
            if (pastPeriod.endsWith(YEAR)) {
                field = Calendar.YEAR;
            } else if (pastPeriod.endsWith(MONTH)) {
                field = Calendar.MONTH;
            }
            c.add(field, -numPeriod);
            return format.format(c.getTime());
        } catch (ParseException e) {
            log.error("Failed to parse {}", pivotDate, e);
        }
        return null;
    }
}
