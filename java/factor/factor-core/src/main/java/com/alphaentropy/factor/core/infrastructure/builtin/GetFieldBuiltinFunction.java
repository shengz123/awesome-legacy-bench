package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.*;

@Lazy
@Repository
@Qualifier("getFieldBiFunc")
public class GetFieldBuiltinFunction extends AbstractBuiltinFunction {

    private static final int THIS_INDEX_PARAM_IDX = 0;
    private static final int FIELD_NAME_PARAM_IDX = 1;
    private static final int PIVOT_OFFSET_PARAM_IDX = 2;
    private static final int WALK_BACK_PARAM_IDX = 3;

    private static final int[] STATIC_PARAMS = new int[]{THIS_INDEX_PARAM_IDX, FIELD_NAME_PARAM_IDX, PIVOT_OFFSET_PARAM_IDX, WALK_BACK_PARAM_IDX};

    @Override
    int[] getStaticParamsIndices() {
        return STATIC_PARAMS;
    }

    @Override
    int getDynamicParamsIndex() {
        return PIVOT_OFFSET_PARAM_IDX;
    }

    @Override
    public Map<Map<String, String>, Object> invoke(FieldAccessor fieldAccessor, CalculationContext context, Map<Map<String, String>, List<String>> params) {
        Map<Map<String, String>, Object> ret = new HashMap<>();
        Map<Integer, String> staticParams = getStaticParams(params);
        Map<String, Collection<Map<String, String>>> dynamicParamsIds = getGroupedDynamicParams(params);

        //Mandatory fields
        boolean isIndex = staticParams.get(THIS_INDEX_PARAM_IDX).equals("index");
        String fieldName = staticParams.get(FIELD_NAME_PARAM_IDX);
        String walkBackStr = staticParams.get(WALK_BACK_PARAM_IDX);
        boolean walkBack = false;
        if (walkBackStr != null) {
            walkBack = Boolean.parseBoolean(walkBackStr);
        }
        if (isIndex) {
            Map<Map<String, String>, String> indexFieldValues;
            // No offset specified
            if (dynamicParamsIds.isEmpty()) {
                Collection<Map<String, String>> indexIds = new HashSet<>(context.getInstrumentIndexIdMappings().values());
                indexFieldValues = fieldAccessor.get(fieldName, indexIds, context.getDate(), walkBack);
                for (Map<String, String> id : context.getInstrumentIndexIdMappings().keySet()) {
                    Map<String, String> indexId = context.getInstrumentIndexIdMappings().get(id);
                    ret.put(id, indexFieldValues.get(indexId));
                }
            } else {
                for (String dynamicParam : dynamicParamsIds.keySet()) {
                    int pivotOffset = Integer.parseInt(dynamicParam);
                    Collection<Map<String, String>> ids = dynamicParamsIds.get(dynamicParam);
                    Collection<Map<String, String>> indexIds = new HashSet<>();
                    for (Map<String, String> id : ids) {
                        indexIds.add(context.getInstrumentIndexIdMappings().get(id));
                    }
                    if (pivotOffset == 0) {
                        indexFieldValues = fieldAccessor.get(fieldName, indexIds, context.getDate(), walkBack);
                    } else {
                        indexFieldValues = fieldAccessor.getByOffset(fieldName, indexIds, context.getDate(), pivotOffset);
                    }
                    for (Map<String, String> id : ids) {
                        Map<String, String> indexId = context.getInstrumentIndexIdMappings().get(id);
                        ret.put(id, indexFieldValues.get(indexId));
                    }
                }
            }
        } else {
            Map<Map<String, String>, String> fieldValues;
            // No offset specified
            if (dynamicParamsIds.isEmpty()) {
                fieldValues = fieldAccessor.get(fieldName, context.getInstrumentIndexIdMappings().keySet(), context.getDate(), walkBack);
                ret.putAll(fieldValues);
            } else {
                for (String dynamicParam : dynamicParamsIds.keySet()) {
                    int pivotOffset = Integer.parseInt(dynamicParam);
                    Collection<Map<String, String>> ids = dynamicParamsIds.get(dynamicParam);
                    if (pivotOffset == 0) {
                        fieldValues = fieldAccessor.get(fieldName, ids, context.getDate(), walkBack);
                    } else {
                        fieldValues = fieldAccessor.getByOffset(fieldName, ids, context.getDate(), pivotOffset);
                    }
                    ret.putAll(fieldValues);
                }
            }
        }
        return ret;
    }

    @Override
    public int[] referenceFieldsIndices() {
        return new int[]{FIELD_NAME_PARAM_IDX};
    }

    @Override
    public int maxParameters() {
        return 4;
    }

    @Override
    public int minParameters() {
        return 2;
    }
}
