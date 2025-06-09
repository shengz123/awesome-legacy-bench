package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Repository
@Qualifier("ipoBiFunc")
@Slf4j
public class NewIPOBuiltinFunction extends AbstractBuiltinFunction {

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
            Map<Map<String, String>, String> day1MapForInstruments = fieldAccessor.getDay1(context.getInstrumentIndexIdMappings().keySet(), null);
            Map<Map<String, String>, String> firstDayMapForIndices = fieldAccessor.getDay1(context.getInstrumentIndexIdMappings().keySet(), startDate);
            // compare instruments' day1 against index's day1
            day1MapForInstruments.forEach((id, day1) -> {
                Map<String, String> indexId = context.getInstrumentIndexIdMappings().get(id);
                String firstDayForIndex = firstDayMapForIndices.get(indexId);
                if (isIPOAfterFirstDay(day1, firstDayForIndex)) {
                    ret.put(id, "Y");
                } else {
                    ret.put(id, "N");
                }
            });
        }
        return ret;
    }

    private boolean isIPOAfterFirstDay(String day1, String indexDay1) {
        return Integer.parseInt(day1) > Integer.parseInt(indexDay1);
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
