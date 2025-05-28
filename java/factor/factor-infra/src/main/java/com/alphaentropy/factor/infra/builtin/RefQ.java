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
@Builtin("RefQ")
public class RefQ extends AbstractBuiltFunction {
    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate, String defaultScope, List<String> params, Map<String, Object> contextParams) {

        Map<String, Object> ret = new HashMap<>();
        Class table = findClassFromVariable(defaultScope, params.get(0).trim());
        String quarterlyMetrics = findAttrFromVariable(defaultScope, params.get(0).trim());

        int movingForwardQSize = 0;
        if (params.size() == 2) {
            movingForwardQSize = Integer.parseInt(params.get(1).trim());
        }

        int fillInType = 0;
        if (params.size() == 3) {
            fillInType = Integer.parseInt(params.get(2).trim());
        }

        for (String symbol : symbols) {
            Pair<Date, Object> objectPair;

            if (movingForwardQSize == 0) {
                objectPair = accessor.pointQ(symbol, table, quarterlyMetrics, valueDate);
            } else {
                objectPair = accessor.pointQ(symbol, table, quarterlyMetrics, valueDate, -movingForwardQSize);
            }

            if (objectPair != null) {
                ret.put(symbol, new BigDecimal(objectPair.getValue().toString()));
            } else {
                if (fillInType == 0) {
                    ret.put(symbol, null);
                } else if (fillInType == 1) {
                    ret.put(symbol, 0);
                } else if (fillInType == 2) {
                    for (int i = 1; i < 5; ++i) {
                        objectPair = accessor.pointQ(symbol, table, quarterlyMetrics, valueDate, -(movingForwardQSize + i));
                        if (objectPair != null) {
                            ret.put(symbol, new BigDecimal(objectPair.getValue().toString()));
                            break;
                        }
                    }
                    if (objectPair == null) {
                        ret.put(symbol, null);
                    }
                }
            }
        }
        return ret;
    }
}
