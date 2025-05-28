package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.common.utils.DateTimeUtil;
import com.alphaentropy.domain.annotation.Builtin;
import com.alphaentropy.store.cache.DataAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
@Builtin("Diff")
// If the report date is Q1, use the value as is
// If the report date is Q2~Q4, use the value - previous value
// the input value date must be quarter end report date
public class Diff extends AbstractBuiltFunction {

    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate,
                                      String defaultScope, List<String> params, Map<String, Object> contextParams) {
        Map<String, Object> ret = new HashMap<>();
        Class clz = findClassFromVariable(defaultScope, params.get(0).trim());
        String attr = findAttrFromVariable(defaultScope, params.get(0).trim());

        int movingForwardQSize = 0;
        if (params.size() == 2) {
            movingForwardQSize = Integer.parseInt(params.get(1).trim());
        }

        for (String symbol : symbols) {
            Pair<Date, Object> pair = accessor.pointQ(symbol, clz, attr, valueDate, -movingForwardQSize);
            if (pair != null) {
                Date d = pair.getKey();
                if (!DateTimeUtil.isQ1(d)) {
                    Pair<Date, Object> prevPair = accessor.pointQ(symbol, clz, attr, valueDate, -(1 + movingForwardQSize));
                    if (prevPair != null) {
                        ret.put(symbol, new BigDecimal(pair.getValue().toString())
                                .subtract(new BigDecimal(prevPair.getValue().toString())));
                    } else {
                        ret.put(symbol, new BigDecimal(pair.getValue().toString()).subtract(new BigDecimal(0)));
                    }
                } else {
                    ret.put(symbol, new BigDecimal(pair.getValue().toString()));
                }
            }
        }
        return ret;
    }
}
