package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Repository
@Qualifier("tdpBiFunc")
public class TotalDataPointsBuiltinFunction extends AbstractBuiltinFunction {
    private static final int PAST_PERIOD_PARAM_IDX = 0;
    private static final int[] STATIC_PARAMS = new int[]{PAST_PERIOD_PARAM_IDX};

    @Override
    int[] getStaticParamsIndices() {
        return STATIC_PARAMS;
    }

    @Override
    int getDynamicParamsIndex() {
        return -1;
    }

    @Override
    public Map<Map<String, String>, Object> invoke(FieldAccessor fieldAccessor, CalculationContext context, Map<Map<String, String>, List<String>> params) {
        Map<Map<String, String>, Object> ret = new HashMap<>();
        Map<Integer, String> staticParams = getStaticParams(params);
        if (staticParams.size() == 1) {
            String pastPeriod = staticParams.get(PAST_PERIOD_PARAM_IDX);
            String startDate = resolveStartDate(context.getDate(), pastPeriod);
            Map<Map<String, String>, String> day1Map = fieldAccessor.getDay1(context.getInstrumentIndexIdMappings().keySet(), null);

            Map<Map<String, String>, String> startDateMap = new HashMap<>();
            // compare instruments' day1 against start date
            day1Map.forEach((id, day1) -> {
                if (isDay1Earlier(day1, startDate)) {
                    startDateMap.put(id, startDate);
                } else {
                    startDateMap.put(id, day1);
                }
            });

            startDateMap.forEach((id, fromDate) -> {
                int tdp = fieldAccessor.getIndexEntriesCount(context.getDate(), id, fromDate);
                ret.put(id, tdp);
            });
        }
        return ret;
    }

    private boolean isDay1Earlier(String day1, String startDate) {
        return Integer.parseInt(day1) < Integer.parseInt(startDate);
    }

    @Override
    public int[] referenceFieldsIndices() {
        return new int[0];
    }

    @Override
    public int maxParameters() {
        return 1;
    }

    @Override
    public int minParameters() {
        return 1;
    }
}
