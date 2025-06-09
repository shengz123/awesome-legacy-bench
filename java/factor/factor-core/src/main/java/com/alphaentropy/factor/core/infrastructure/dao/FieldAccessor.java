package com.alphaentropy.factor.core.infrastructure.dao;

import org.apache.commons.math3.util.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public interface FieldAccessor {
    Map<Map<String, String>, String> get(String fieldName, Collection<Map<String, String>> instrumentIds, String date, boolean walkBack);
    Map<Map<String, String>, String> getByOffset(String fieldName, Collection<Map<String, String>> instrumentIds, String pivotDate, int offset);
    boolean exists(Map<String, String> instrument, String field);
    Map<Map<String, String>, TreeMap<String, String>> getPast(String fieldName, Collection<Map<String, String>> instrumentIds, String date, int pastPeriod, boolean walkBack);
    Map<Map<String, String>, TreeMap<String, Pair<String, String>>> getPastPair(String fieldName, Collection<Map<String, String>> instrumentIds, String date, int pastPeriod);
    Map<Map<String, String>, TreeMap<String, Pair<String, String>>> getPastPair2(String fieldName, String indexFieldName, Collection<Map<String, String>> instrumentIds, String date, int pastPeriod);
    Map<Map<String, String>, String> getDay1(Collection<Map<String, String>> instrumentIds, String fromDate);
    Integer getIndexEntriesCount(String date, Map<String, String> instrumentId, String from);
    Map<Map<String, String>, Integer> getGapDays(String date, Collection<Map<String, String>> instrumentIds);
    void put(Map<Map<String, String>, Map<String, String>> fieldValues, String date);
}
