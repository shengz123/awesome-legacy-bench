package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.domain.annotation.Builtin;
import com.alphaentropy.store.cache.DataAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
@Builtin("GET")
public class GetData extends AbstractBuiltFunction {

    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate,
                                      String defaultScope, List<String> params, Map<String, Object> contextParams) {
        Map<String, Object> ret = new HashMap<>();
        Class clz = findClassFromVariable(defaultScope, params.get(0).trim());
        String attr = findAttrFromVariable(defaultScope, params.get(0).trim());

        int index = 0;
        if (params.size() >= 2) {
            index = Integer.valueOf(params.get(1).trim());
        }

        BigDecimal defaultValue = null;
        if (params.size() == 3) {
            defaultValue = new BigDecimal(params.get(2).trim());
        }

        for (String symbol : symbols) {
            Object value = null;
            if (index == 0) {
                value = accessor.point(symbol, clz, attr, valueDate);
            } else {
                value = accessor.point(symbol, clz, attr, valueDate, index);
            }
            if (value != null) {
                ret.put(symbol, value);
            } else {
                ret.put(symbol, defaultValue);
            }
        }
        return ret;
    }
}
