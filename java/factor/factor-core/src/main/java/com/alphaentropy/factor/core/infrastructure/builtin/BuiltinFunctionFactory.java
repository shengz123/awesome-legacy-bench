package com.alphaentropy.factor.core.infrastructure.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Lazy
@Component
public class BuiltinFunctionFactory {
    public static final String GET_FIELD = "get";
    public static final String GET_PAST = "past";
    public static final String GET_PAIR = "pair";
    public static final String GET_PAIR2 = "pair2";
    public static final String TOTAL_DATA_POINTS = "total_data_points";
    public static final String GAP_DAYS = "gap_days";
    public static final String NEW_IPO = "new_ipo";

    @Autowired
    @Qualifier("gapDaysBiFunc")
    BuiltinFunction gapDaysFunction;

    @Autowired
    @Qualifier("getFieldBiFunc")
    BuiltinFunction getFieldFunction;

    @Autowired
    @Qualifier("getPair2BiFunc")
    BuiltinFunction getPair2Function;

    @Autowired
    @Qualifier("getPairBiFunc")
    BuiltinFunction getPairFunction;

    @Autowired
    @Qualifier("ipoBiFunc")
    BuiltinFunction newIPOFunction;

    @Autowired
    @Qualifier("getPastBiFunc")
    BuiltinFunction getPastFunction;

    @Autowired
    @Qualifier("tdpBiFunc")
    BuiltinFunction totalDataPointsFunction;

    private Map<String, BuiltinFunction> functionMap;

    @PostConstruct
    public void init() {
        this.functionMap = new HashMap<>();
        functionMap.put(GET_FIELD, getFieldFunction);
        functionMap.put(TOTAL_DATA_POINTS, totalDataPointsFunction);
        functionMap.put(NEW_IPO, newIPOFunction);
        functionMap.put(GAP_DAYS, gapDaysFunction);
        functionMap.put(GET_PAST, getPastFunction);
        functionMap.put(GET_PAIR, getPairFunction);
        functionMap.put(GET_PAIR2, getPair2Function);
    }

    public Set<String> getAllFunctions() {
        return functionMap.keySet();
    }

    public BuiltinFunction getBuiltinFunction(String name) {
        return functionMap.get(name);
    }
}
