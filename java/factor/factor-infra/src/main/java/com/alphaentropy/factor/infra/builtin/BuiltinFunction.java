package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.store.cache.DataAccessor;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BuiltinFunction {
    Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate,
                               String defaultScope, List<String> params, Map<String, Object> contextParams);
}
