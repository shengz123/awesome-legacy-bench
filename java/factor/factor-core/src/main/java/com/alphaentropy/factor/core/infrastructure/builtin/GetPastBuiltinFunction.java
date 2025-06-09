package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.*;

@Lazy
@Repository
@Qualifier("getPastBiFunc")
public class GetPastBuiltinFunction extends AbstractBuiltinFunction{

    private static final int FIELD_NAME_PARAM_INX = 0;
    private static final int PAST_PERIOD_PARAM_INX = 1;
    private static final int USE_WB_PARAM_INX = 2;
    private static final int[] STATIC_PARAMS = new int[] {FIELD_NAME_PARAM_INX, PAST_PERIOD_PARAM_INX, USE_WB_PARAM_INX};

    @Override
    int[] getStaticParamsIndices() {
        return STATIC_PARAMS;
    }

    @Override
    int getDynamicParamsIndex() {
        return PAST_PERIOD_PARAM_INX;
    }

    @Override
    public Map<Map<String, String>, Object> invoke(FieldAccessor fieldAccessor, CalculationContext context, Map<Map<String, String>, List<String>> params) {
        Map<Map<String, String>, Object> ret = new HashMap<>();
        Map<Integer, String> staticParams = getStaticParams(params);
        Map<String, Collection<Map<String, String>>> dynamicParamsIds = getGroupedDynamicParams(params);
        boolean isIndex = false;
        boolean useWalkBack = true;
        String fieldName = staticParams.get(FIELD_NAME_PARAM_INX);
        if (fieldName.startsWith("index.")) {
            isIndex = true;
            fieldName = fieldName.substring("index.".length());
        }
        String useWB = staticParams.get(USE_WB_PARAM_INX);
        if (StringUtils.isNotEmpty(useWB)) {
            useWalkBack = Boolean.parseBoolean(useWB);
        }
        if (isIndex) {
            for (String dynamicParam  : dynamicParamsIds.keySet()) {
                Collection<Map<String, String>> ids = dynamicParamsIds.get(dynamicParam);
                int pastPeriod = Integer.parseInt(dynamicParam);
                Collection<Map<String, String>> indexIds = new HashSet<>();
                for (Map<String, String> id : ids) {
                    indexIds.add(context.getInstrumentIndexIdMappings().get(id));
                }
                Map<Map<String, String>, TreeMap<String, String>> indexFieldValues = fieldAccessor.getPast(fieldName, indexIds, context.getDate(), pastPeriod, useWalkBack);
                for (Map<String, String> id : ids) {
                    Map<String, String> indexId = context.getInstrumentIndexIdMappings().get(id);
                    TreeMap<String, String> dateFields = indexFieldValues.get(indexId);
                    if (dateFields != null) {
                        ret.put(id, new ArrayList<>(dateFields.descendingMap().values()));
                    }
                }
            }
        } else {
            for (String dynamicParam  : dynamicParamsIds.keySet()) {
                Collection<Map<String, String>> ids = dynamicParamsIds.get(dynamicParam);
                int pastPeriod = Integer.parseInt(dynamicParam);
                Map<Map<String, String>, TreeMap<String, String>> fieldValues = fieldAccessor.getPast(fieldName, ids, context.getDate(), pastPeriod, useWalkBack);
                fieldValues.forEach((id, dateFields) -> ret.put(id, new ArrayList<>(dateFields.descendingMap().values())));
            }
        }
        return ret;
    }

    @Override
    public int[] referenceFieldsIndices() {
        return new int[] {FIELD_NAME_PARAM_INX};
    }

    @Override
    public int maxParameters() {
        return 3;
    }

    @Override
    public int minParameters() {
        return 1;
    }
}
