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
@Builtin("AccQ")
public class AccQ extends AbstractBuiltFunction {

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
            BigDecimal retValue = new BigDecimal(0);

            for (Date curDate = valueDate; !DateTimeUtil.isYearEnd(curDate); curDate = DateTimeUtil.getPrevQuarter(curDate)) {
                Pair<Date, Object> tmp = accessor.pointQ(symbol, clz, attr, curDate, movingForwardQSize);
                if (tmp != null) {
                    retValue = retValue.add(new BigDecimal(tmp.getValue().toString()));
                }
            }

            ret.put(symbol, retValue);
        }
        return ret;
    }
}
