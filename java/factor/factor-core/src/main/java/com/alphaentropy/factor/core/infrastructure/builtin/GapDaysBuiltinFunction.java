package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Lazy
@Repository
@Qualifier("gapDaysBiFunc")
public class GapDaysBuiltinFunction implements BuiltinFunction {
    @Override
    public Map<Map<String, String>, Object> invoke(FieldAccessor fieldAccessor, CalculationContext context, Map<Map<String, String>, List<String>> params) {
        return fieldAccessor.getGapDays(context.getDate(), context.getInstrumentIndexIdMappings().keySet()).entrySet()
                .stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString()));
    }

    @Override
    public int[] referenceFieldsIndices() {
        return new int[0];
    }

    @Override
    public int maxParameters() {
        return 0;
    }

    @Override
    public int minParameters() {
        return 0;
    }
}
