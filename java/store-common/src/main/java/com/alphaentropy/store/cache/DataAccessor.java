package com.alphaentropy.store.cache;

import org.apache.commons.math3.util.Pair;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public interface DataAccessor {
    Collection<String> getAllSymbols();
    Object point(String symbol, Class clz, String attr, Date date);
    Object point(String symbol, Class clz, String attr, Date date, int offset);
    Pair<Date, Object> pointQ(String symbol, Class clz, String attr, Date date);
    Pair<Date, Object> pointQ(String symbol, Class clz, String attr, Date date, int offset);
    TreeMap<Date, Object> past(String symbol, Class clz, Date date, int pastDays);
    TreeMap<Date, Object> past(String symbol, Class clz, String attr, Date date, int pastDays);
    TreeMap<Date, Pair<Object, Object>> pair(String symbol, Class left, String leftAttr, Class right, String rightAttr,
                                             Date date, int pastDays);
    Pair<Object, Object> pairPoint(String symbol, Class left, String leftAttr, Class right, String rightAttr,
                                   Date date, int offset);
    void put(String symbol, Date date, String name, Object val);
    void put(String symbol, Date date, Map<String, Object> multiValues, String frequency);

}
