package com.alphaentropy.common.utils;

import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
public class LoadFilter {
    public static List filter(List orig, Date latest, Class clz) {
        if (orig == null || orig.isEmpty()) {
            return null;
        }
        List result = new ArrayList();
        Field[] fields = clz.getDeclaredFields();
        Field dateField = null;
        try {
            for (Field field : fields) {
                if (field.getAnnotation(MySQLDateKey.class) != null) {
                    dateField = field;
                    dateField.setAccessible(true);
                }
            }
            for (Object row : orig) {
                Date dateInRow = (Date) dateField.get(row);
                if (latest == null || latest.getTime() < dateInRow.getTime()) {
                    result.add(row);
                }
            }
        } catch (Exception e) {
            log.error("Failed to filter for class " + clz.getSimpleName(), e);
        }
        return result;
    }

    // Used in daily load, skip the dates earlier than the latest date from the web loading
    public static String getEarliestDate(Map<String, Date> symbolDates, String symbol, String day1,
                                         String dateFmt) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        Date ret = sdf.parse(day1);
        Date symbolDate = symbolDates.get(symbol);
        if (symbolDate != null && !ret.after(symbolDate)) {
            return sdf.format(symbolDate);
        }
        return day1;
    }

    public static String getEarliestDate(Map<String, Date> symbolDates, String day1, String dateFmt) throws Exception {
        if (symbolDates == null || symbolDates.isEmpty()) {
            return day1;
        }
        Date earliest = null;
        for (String symbol : symbolDates.keySet()) {
            Date symbolDate = symbolDates.get(symbol);
            if (earliest == null || symbolDate.before(earliest)) {
                earliest = symbolDate;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        Date ret = sdf.parse(day1);
        if (earliest != null && !ret.after(earliest)) {
            return sdf.format(earliest);
        }
        return day1;
    }

    public static List filter(List orig, Map<String, Date> symbolDates, Class clz) {
        if (orig == null || orig.isEmpty()) {
            return null;
        }
        List result = new ArrayList();
        Field[] fields = clz.getDeclaredFields();
        Field dateField = null;
        Field symbolField = null;
        try {
            for (Field field : fields) {
                if (field.getAnnotation(MySQLDateKey.class) != null) {
                    dateField = field;
                    dateField.setAccessible(true);
                }
                if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                    symbolField = field;
                    symbolField.setAccessible(true);
                }
            }
            for (Object row : orig) {
                Date dateInRow = (Date) dateField.get(row);
                String symbol = (String) symbolField.get(row);
                Date latest = symbolDates.get(symbol);
                if (latest == null || latest.getTime() < dateInRow.getTime()) {
                    result.add(row);
                }
            }
        } catch (Exception e) {
            log.error("Failed to filter for class " + clz.getSimpleName(), e);
        }
        return result;
    }
}
