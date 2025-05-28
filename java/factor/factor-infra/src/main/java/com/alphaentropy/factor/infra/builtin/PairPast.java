package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.domain.annotation.Builtin;
import com.alphaentropy.store.cache.DataAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
@Builtin("PAIR")
public class PairPast extends AbstractBuiltFunction {

    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate,
                                      String defaultScope, List<String> params, Map<String, Object> contextParams) {
        Map<String, Object> ret = new HashMap<>();
        Class leftClz = findClassFromVariable(defaultScope, params.get(0).trim());
        String leftAttr = findAttrFromVariable(defaultScope, params.get(0).trim());
        Class rightClz = findClassFromVariable(defaultScope, params.get(1).trim());
        String rightAttr = findAttrFromVariable(defaultScope, params.get(1).trim());
        int pastDays = Integer.valueOf(params.get(2).trim());
        for (String symbol : symbols) {
            TreeMap<Date, Pair<Object, Object>> pair = accessor.pair(symbol, leftClz, leftAttr, rightClz, rightAttr,
                    valueDate, pastDays);
            if (pair.size() == pastDays) {
                ret.put(symbol, pair.descendingMap().values());
            }
        }
        return ret;
    }
}
