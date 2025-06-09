package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.domain.annotation.Builtin;
import com.alphaentropy.store.cache.DataAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@Builtin("PAST")
public class PastData extends AbstractBuiltFunction {

    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate,
                                      String defaultScope, List<String> params, Map<String, Object> contextParams) {
        Map<String, Object> ret = new HashMap<>();
        Class clz = findClassFromVariable(defaultScope, params.get(0).trim());
        String attr = findAttrFromVariable(defaultScope, params.get(0).trim());
        int pastDays = Integer.valueOf(params.get(1).trim());
        for (String symbol : symbols) {
            TreeMap<Date, Object>  pair = accessor.past(symbol, clz, attr, valueDate, pastDays);
            if (pair.size() == pastDays) {
                ret.put(symbol, pair.descendingMap().values());
            }
        }
        return ret;
    }
}
