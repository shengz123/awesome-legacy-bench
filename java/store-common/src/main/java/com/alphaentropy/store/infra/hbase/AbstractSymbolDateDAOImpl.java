package com.alphaentropy.store.infra.hbase;

import com.alphaentropy.common.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public abstract class AbstractSymbolDateDAOImpl implements TabularDAO {
    @Autowired
    protected HBaseStore hBaseStore;

    protected abstract String getTableName();
    protected abstract byte[] getColumnFamilyInBytes();

    private static final String DATE_LIMIT = "99999999";
    private static final String DATE_PATTERN = "yyyyMMdd";

    String encode(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String outputDate = DateTimeUtil.convertDate(sdf.format(date), DATE_PATTERN);
        return String.valueOf(Integer.parseInt(DATE_LIMIT) - Integer.parseInt(outputDate));
    }

    Date decode(String dateStr, SimpleDateFormat sdf) throws ParseException {
        String orig = String.valueOf(Integer.parseInt(DATE_LIMIT) - Integer.parseInt(dateStr));
        return sdf.parse(orig);
    }

    @Override
    public void writeCell(String symbol, Date date, String column, String value) {
        try {
            String rowKey = symbol + encode(date);
            Put put = new Put(Bytes.toBytes(rowKey))
                    .addColumn(getColumnFamilyInBytes(), Bytes.toBytes(column), Bytes.toBytes(value));
            hBaseStore.put(put, getTableName());
        } catch (Exception e) {
            log.error("Failed to write cell of {}, {} for {} with {}", symbol, date, column, value, e);
        }
    }

    @Override
    public void writeRow(String symbol, Date date, Map<String, String> columnValues) {
        try {
            String rowKey = symbol + encode(date);
            Put put = new Put(Bytes.toBytes(rowKey));
            for (String column : columnValues.keySet()) {
                put.addColumn(getColumnFamilyInBytes(), Bytes.toBytes(column), Bytes.toBytes(columnValues.get(column)));
            }
            hBaseStore.put(put, getTableName());
        } catch (Exception e) {
            log.error("Failed to write cell of {}, {} for {}", symbol, date, columnValues, e);
        }
    }

    @Override
    public TreeMap<Date, Map<String, String>> scanPast(String symbol, Date date, List<String> columns, int rowCount) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
            TreeMap<Date, Map<String, String>> ret = new TreeMap<>();
            String rowKey = symbol + encode(date);
            Scan scan = new Scan().withStartRow(Bytes.toBytes(rowKey)).setLimit(rowCount);
            for (String column : columns) {
                scan.addColumn(getColumnFamilyInBytes(), Bytes.toBytes(column));
            }
            ResultScanner results = hBaseStore.scan(scan, getTableName());
            Result result = null;
            while ((result = results.next()) != null) {
                if (result != null && !result.isEmpty()) {
                    Map<String, String> row = new HashMap<>();
                    Date vd = decode(Bytes.toString(result.getRow()).substring(6), sdf);
                    for (Cell cell : result.listCells()) {
                        String column = Bytes.toString(CellUtil.cloneQualifier(cell));
                        String value = Bytes.toString(CellUtil.cloneValue(cell));
                        row.put(column, value);
                    }
                    ret.put(vd, row);
                }
            }
            return ret;
        } catch (Exception e) {
            log.error("Failed to scan past of {}, {} with {}", symbol, date, rowCount, e);
        }
        return null;
    }

    @Override
    public String readCell(String symbol, Date date, String column) {
        try {
            String rowKey = symbol + encode(date);
            Get get = new Get(Bytes.toBytes(rowKey)).addColumn(getColumnFamilyInBytes(), Bytes.toBytes(column));
            Result result = hBaseStore.get(get, getTableName());
            if (result != null && !result.isEmpty()) {
                return Bytes.toString(result.getValue(getColumnFamilyInBytes(), Bytes.toBytes(column)));
            }
        } catch (Exception e) {
            log.error("Failed to read cell of {}, {} for {}", symbol, date, column, e);
        }
        return null;
    }

    @Override
    public Map<String, String> readRow(String symbol, Date date) {
        Map<String, String> ret = new HashMap<>();
        try {
            String rowKey = symbol + encode(date);
            Get get = new Get(Bytes.toBytes(rowKey));
            Result result = hBaseStore.get(get, getTableName());
            if (result != null && !result.isEmpty()) {
                for (Cell cell : result.listCells()) {
                    String column = Bytes.toString(CellUtil.cloneQualifier(cell));
                    String value = Bytes.toString(CellUtil.cloneValue(cell));
                    ret.put(column, value);
                }
            }
            return ret;
        } catch (Exception e) {
            log.error("Failed to read cell of {}, {}", symbol, date, e);
        }
        return null;
    }

    @Override
    public TreeMap<String, Map<String, String>> readMultiRows(String symbol, List<Date> dates) {
        TreeMap<String, Map<String, String>> ret = new TreeMap<>();
        try {
            List<Get> gets = new ArrayList<>();
            for (Date date : dates) {
                String rowKey = symbol + encode(date);
                gets.add(new Get(Bytes.toBytes(rowKey)));
            }
            Result[] results = hBaseStore.get(gets, getTableName());
            for (Result result : results) {
                if (result != null && !result.isEmpty()) {
                    Map<String, String> row = new HashMap<>();
                    String date = Bytes.toString(result.getRow()).substring(6);
                    for (Cell cell : result.listCells()) {
                        String column = Bytes.toString(CellUtil.cloneQualifier(cell));
                        String value = Bytes.toString(CellUtil.cloneValue(cell));
                        row.put(column, value);
                    }
                    ret.put(date, row);
                }
            }
            return ret;
        } catch (Exception e) {
            log.error("Failed to read cell of {}, {}", symbol, dates, e);
        }
        return null;
    }

}
