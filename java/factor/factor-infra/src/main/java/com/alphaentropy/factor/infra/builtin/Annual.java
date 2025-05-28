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
@Builtin("Annual")
public class Annual extends AbstractBuiltFunction {

    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate, String defaultScope, List<String> params, Map<String, Object> contextParams) {
        Map<String, Object> ret = new HashMap<>();
        Class clz = findClassFromVariable(defaultScope, params.get(0).trim());
        String attr = findAttrFromVariable(defaultScope, params.get(0).trim());

        int movingForwardYSize = 1;
        if (params.size() == 2) {
            movingForwardYSize = Integer.parseInt(params.get(1).trim());
        }
        // Date -> last year
        if (!DateTimeUtil.isYearEnd(valueDate)) {
            valueDate = DateTimeUtil.getPrevYearEnd(valueDate);
            movingForwardYSize -= 1;
        }

        if (movingForwardYSize < 0) {
            return ret;
        }

        for (String symbol : symbols) {
            Pair<Date, Object> pair = accessor.pointQ(symbol, clz, attr, valueDate, (movingForwardYSize << 2));
            if (pair != null) {
                ret.put(symbol, new BigDecimal(pair.getValue().toString()));
            } else {
                ret.put(symbol, new BigDecimal(0));
            }
        }
        return ret;
    }
}
