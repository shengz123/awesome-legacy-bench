package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.*;

@Lazy
@Repository
@Qualifier("getPairBiFunc")
@Slf4j
public class GetPairBuiltinFunction extends AbstractBuiltinFunction{

    private static final int FIELD_NAME_PARAM_IDX = 0;
    private static final int PAST_PERIOD_LOW_PARAM_IDX = 1;
    private static final int PAST_PERIOD_HIGH_PARAM_IDX = 2;

    private static final int[] STATIC_PARAMS = new int[] {FIELD_NAME_PARAM_IDX, PAST_PERIOD_LOW_PARAM_IDX, PAST_PERIOD_HIGH_PARAM_IDX};


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
        if (staticParams.size() == STATIC_PARAMS.length) {
            String fieldName = staticParams.get(FIELD_NAME_PARAM_IDX);
            int pastPeriod = Integer.parseInt(staticParams.get(PAST_PERIOD_HIGH_PARAM_IDX));
            int pastPeriodThreshold = Integer.parseInt(staticParams.get(PAST_PERIOD_LOW_PARAM_IDX));
            Map<Map<String, String>, TreeMap<String, Pair<String, String>>> fieldValues = fieldAccessor.getPastPair(
                    fieldName, context.getInstrumentIndexIdMappings().keySet(), context.getDate(), pastPeriod);
            // remove the rows whose count is not match with pastPeriod
            Collection<Map<String, String>> ids = new ArrayList<>(fieldValues.keySet());
            for (Map<String, String> id :ids) {
                TreeMap<String, Pair<String, String>> dateValues = fieldValues.get(id);
                if (dateValues.size() < pastPeriodThreshold) {
                    log.debug("Insufficient dates ({} out of {}) for instrument {}.", dateValues.size(), pastPeriodThreshold, id);
                } else {
                    ret.put(id, new ArrayList<>(dateValues.descendingMap().values()));
                }
            }
        }
        return ret;
    }

    @Override
    public int[] referenceFieldsIndices() {
        return new int[] {FIELD_NAME_PARAM_IDX};
    }

    @Override
    public int maxParameters() {
        return 3;
    }

    @Override
    public int minParameters() {
        return 3;
    }
}
