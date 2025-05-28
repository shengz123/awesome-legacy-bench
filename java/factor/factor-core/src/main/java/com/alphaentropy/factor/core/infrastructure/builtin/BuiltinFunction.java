package com.alphaentropy.factor.core.infrastructure.builtin;

import com.alphaentropy.factor.core.domain.CalculationContext;
import com.alphaentropy.factor.core.infrastructure.dao.FieldAccessor;

import java.util.List;
import java.util.Map;

public interface BuiltinFunction {
    Map<Map<String, String>, Object> invoke(FieldAccessor fieldAccessor, CalculationContext context, Map<Map<String, String>, List<String>> params);
    int[] referenceFieldsIndices();
    int maxParameters();
    int minParameters();
}
