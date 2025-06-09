package com.alphaentropy.store.infra.hbase;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface TabularDAO {
    void writeCell(String symbol, Date date, String column, String value);
    void writeRow(String symbol, Date date, Map<String, String> columnValues);

    String readCell(String symbol, Date date, String column);
    TreeMap<Date, Map<String, String>> scanPast(String symbol, Date date, List<String> column, int rowCount);
    Map<String, String> readRow(String symbol, Date date);
    TreeMap<String, Map<String, String>> readMultiRows(String symbol, List<Date> dates);
}
