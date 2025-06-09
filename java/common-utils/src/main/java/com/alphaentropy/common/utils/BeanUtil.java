package com.alphaentropy.common.utils;

import com.alphaentropy.domain.annotation.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.alphaentropy.common.utils.NameUtil.camelToUnix;

@Slf4j
public class BeanUtil {

    private static final String PKG_DOMAIN = "com.alphaentropy.domain";

    public static List<Class> getPreLoadClasses() throws Exception {
        List<Class> ret = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Cached.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(PKG_DOMAIN)) {
            Class clz = Class.forName(bd.getBeanClassName());
            Cached cached = (Cached) clz.getAnnotation(Cached.class);
            if (cached.preLoaded()) {
                ret.add(clz);
            }
        }
        return ret;
    }

    public static Map<Class, Class> getClassesWithEffectiveClz() throws Exception {
        Map<Class, Class> ret = new HashMap<>();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Cached.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(PKG_DOMAIN)) {
            Class clz = Class.forName(bd.getBeanClassName());
            Cached cached = (Cached) clz.getAnnotation(Cached.class);
            if (!cached.effectiveClz().equals(String.class)) {
                ret.put(clz, cached.effectiveClz());
            }
        }
        return ret;
    }

    public static String getEffectiveDateField(Class clz) {
        Cached cached = (Cached) clz.getAnnotation(Cached.class);
        if (cached != null) {
            return cached.effectiveDate();
        }
        return null;
    }

    public static List<Class> findClassesWithAnnotation(String pkg, Class annoClz) {
        List<Class> ret = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner
                = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annoClz));
        for (BeanDefinition bd : scanner.findCandidateComponents(pkg)) {
            Class clz = null;
            try {
                clz = Class.forName(bd.getBeanClassName());
            } catch (ClassNotFoundException e) {
                log.error("Failed to load class {}", bd.getBeanClassName(), e);
            }
            ret.add(clz);
        }
        return ret;
    }

    public static Class findClassWithAnnotation(Class annoClz, String clzName) {
        List<Class> candidates = findClassesWithAnnotation(PKG_DOMAIN, annoClz);
        for (Class clz : candidates) {
            if (clz.getSimpleName().equals(clzName)) {
                return clz;
            }
        }
        return null;
    }

    public static List<Class> getCachedClassesByFrequency(String frequency) throws Exception {
        List<Class> ret = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner
                = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Cached.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(PKG_DOMAIN)) {
            Class clz = Class.forName(bd.getBeanClassName());
            Cached cached = (Cached) clz.getAnnotation(Cached.class);
            if (frequency.equals(cached.frequency())) {
                ret.add(clz);
            }
        }
        return ret;
    }

    public static TreeMap<Date, Object> convertList(List list, Class clz) {
        TreeMap<Date, Object> ret = new TreeMap<>();
        for (Object object : list) {
            Date date = getDate(object, clz);
            ret.put(date, object);
        }
        return ret;
    }

    public static String getCacheFrequency(Class clz) {
        Cached cached = (Cached) clz.getAnnotation(Cached.class);
        return cached.frequency();
    }

    // Axis: 0 - by row; 1 - by column
    public static List mapDataFrameToBeans(CSVDataFrame dataFrame, Class clz, String dateFmt, int axis, int numSkipped,
                                          boolean mandateDate, @Nullable String givenSymbol,
                                          @Nullable String givenDate, @Nullable String stopNumber,
                                           boolean alignFromRight) {
        List list = new ArrayList();
        try {
            for (int i = numSkipped; i < dataFrame.numSlices(axis); i++) {
                Object val = mapRowToBean(dataFrame.getSlice(i, axis), clz, dateFmt, mandateDate, givenSymbol,
                        givenDate, stopNumber, alignFromRight);
                if (val != null) {
                    list.add(val);
                }
            }
        } catch (Exception e) {
            log.error("Failed to map data frame to beans", e);
        }
        return list;
    }

    public static List mapDataFrameToBeans(List<Map> list, Class clz, String dateFmt,
                                           boolean mandateDate, @Nullable String givenSymbol,
                                           @Nullable String givenDate, @Nullable String stopNumber) {
        List ret = new ArrayList();
        try {
            for (Map row : list) {
                Object val = mapJsonToBean(row, clz, dateFmt, mandateDate, givenSymbol, givenDate, stopNumber);
                if (val != null) {
                    ret.add(val);
                }
            }
        } catch (Exception e) {
            log.error("Failed to map data frame to beans", e);
        }
        return ret;
    }

    public static Object mapBeanToAnother(Object orig, Class srcClz, Class destClz, Map<String, String> fieldMapping) {
        try {
            Object ret = BeanUtils.instantiateClass(destClz);
            for (String srcField : fieldMapping.keySet()) {
                Field destField = destClz.getField(fieldMapping.get(srcField));
                Field field = srcClz.getField(srcField);
                Object fieldValue = field.get(orig);
                Method setter = destClz.getDeclaredMethod(setterName(destField.getName()), destField.getType());
                setter.invoke(ret, fieldValue);
            }
            return ret;
        } catch (Exception e) {
            log.error("Failed to map {} to {}", srcClz.getSimpleName(), destClz.getSimpleName(), e);
        }
        return null;
    }

    public static Date getDate(Object object, Class clz) {
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                field.setAccessible(true);
                try {
                    return (Date) field.get(object);
                } catch (Exception e) {
                    log.error("Failed to get date {} from {}", field.getName(), object);
                }
            }
        }
        return null;
    }

    public static Date getReportDate(Object object, Class clz) throws Exception {
        Field field = clz.getDeclaredField("reportDate");
        field.setAccessible(true);
        return (Date) field.get(object);
    }

    public static String getSymbol(Object object, Class clz) throws Exception {
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                field.setAccessible(true);
                return (String) field.get(object);
            }
        }
        return null;
    }

    public static String getFrequency(Class clz) {
        Cached cached = (Cached) clz.getAnnotation(Cached.class);
        if (cached != null) {
            return cached.frequency();
        }
        return null;
    }

    public static void setSymbol(Object object, Class clz, String symbol) throws Exception {
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                Method setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                setter.invoke(object, symbol);
                return;
            }
        }
    }

    public static Object getFieldValue(Class clz, Object object, String attributeName) {
        try {
            Field field = clz.getDeclaredField(attributeName);
            field.setAccessible(true);
            Factor factor = field.getAnnotation(Factor.class);
            if (factor != null) {
                String defaultVal = factor.defaultVal();
                if (object == null ) {
                    return new BigDecimal(defaultVal);
                } else {
                    Object val = field.get(object);
                    if (val == null) {
                        return new BigDecimal(defaultVal);
                    }
                }
            }
            return field.get(object);
        } catch (Exception e) {
            log.error("Failed to get field {} from {}", attributeName, clz.getSimpleName());
        }
        return null;
    }

    public static Object mapJsonToBean(Map map, Class clz, String dateFmt,
                                       boolean mandateDate, @Nullable String givenSymbol,
                                       @Nullable String givenDate, @Nullable String stopNumber) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        Object val = clz.newInstance();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            Method setter = null;
            String cell = null;
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                String symbol = null;
                if (givenSymbol != null) {
                    symbol = givenSymbol;
                } else {
                    if (fieldNotExists(field, map)) {
                        log.error("{} with no field {} available in {} ", clz.getSimpleName(), field.getName(), map);
                        return null;
                    }
                    symbol = getFieldValue(field, map).toString();
                }
                setter.invoke(val, symbol);
            } else if (field.getAnnotation(MySQLDateKey.class) != null) {
                Date date = null;
                if (givenDate != null) {
                    date = sdf.parse(givenDate);
                } else {
                    if (fieldNotExists(field, map)) {
                        log.error("{} with no field {} available in {} ", clz.getSimpleName(), field.getName(), map);
                        return null;
                    }

                    cell = getFieldValue(field, map).toString();
                    try {
                        date = sdf.parse(cell);
                    } catch (ParseException e) {
                        if (mandateDate) {
                            return null;
                        } else {
                            break;
                        }
                    }
                }
                setter = clz.getDeclaredMethod(setterName(field.getName()), Date.class);
                setter.invoke(val, date);
            } else {
                Object obj = getFieldValue(field, map);
                if (obj != null) {
                    cell = obj.toString();
                    if (field.getType().equals(BigDecimal.class)) {
                        if (stopNumber == null || !cell.contains(stopNumber)) {
                            setter = clz.getDeclaredMethod(setterName(field.getName()), BigDecimal.class);
                            setter.invoke(val, new BigDecimal(cell.replace(",", "").replace("%", "")));
                        }
                    } else if (field.getType().equals(Date.class)) {
                        try {
                            Date dateVal = sdf.parse(cell);
                            setter = clz.getDeclaredMethod(setterName(field.getName()), Date.class);
                            setter.invoke(val, sdf.parse(cell));
                        } catch (ParseException e) {
                            log.error("Cannot parse date from {} for {} in row {}", cell, field.getName(), map);
                        }
                    } else {
                        setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                        setter.invoke(val, cell);
                    }
                }
            }
        }
        return val;
    }

    private static boolean fieldNotExists(Field field, Map map) {
        return (!map.containsKey(camelToUnix(field.getName()).toUpperCase())
                    && !map.containsKey(camelToUnix(field.getName()).toLowerCase()))
                || (map.get(camelToUnix(field.getName()).toUpperCase()) == null
                    && map.get(camelToUnix(field.getName()).toLowerCase()) == null);
    }

    private static Object getFieldValue(Field field, Map map) {
        Object obj = map.get(camelToUnix(field.getName()).toUpperCase());
        if (obj == null) {
            obj = map.get(camelToUnix(field.getName()).toLowerCase());
        }
        return obj;
    }

    // Assumption:
    // row element sequence is same as the bean field sequence
    // 1. Symbol: could be given or from the row
    // 2. Date: could be given or from the row
    // 3. Other fields
    public static Object mapRowToBean(String[] row, Class clz, String dateFmt,
                                      boolean mandateDate, @Nullable String givenSymbol,
                                      @Nullable String givenDate, @Nullable String stopNumber,
                                      boolean alignFromRight) throws Exception {
        if (alignFromRight) {
            return mapRowToBeanFromRight(row, clz, dateFmt, mandateDate, givenSymbol, givenDate, stopNumber);
        } else {
            return mapRowToBeanFromLeft(row, clz, dateFmt, mandateDate, givenSymbol, givenDate, stopNumber);
        }
    }

    private static String getCellValueFromRight(String[] row, int rowIdx) {
        if (rowIdx > 0) {
            return row[rowIdx];
        } else if (rowIdx == 0){
            StringBuffer sb = new StringBuffer();
            for (int i = rowIdx; i >=0 ; i--) {
                sb.append(row[i]);
            }
            return sb.toString();
        }
        return null;
    }

    private static Object mapRowToBeanFromRight(String[] row, Class clz, String dateFmt, boolean mandateDate,
                                               @Nullable String givenSymbol, @Nullable String givenDate,
                                               @Nullable String stopNumber) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        Object val = clz.newInstance();
        Field[] fields = clz.getDeclaredFields();
        int counter = row.length - 1;
        for (int i = fields.length - 1; i >=0; i--) {
            Field field = fields[i];
            Method setter = null;
            String cell = getCellValueFromRight(row, counter);
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                setter.invoke(val, givenSymbol);
            } else if (field.getAnnotation(MySQLDateKey.class) != null) {
                try {
                    Date date = sdf.parse(givenDate);
                    setter = clz.getDeclaredMethod(setterName(field.getName()), Date.class);
                    setter.invoke(val, date);
                } catch (ParseException e) {
                    log.error("Failed to convert {} to date, row {}, {}, {}", givenDate, row, givenSymbol, givenDate);
                    if (mandateDate) {
                        return null;
                    }
                }
            } else {
                if (field.getType().equals(BigDecimal.class)) {
                    if (stopNumber == null || !cell.trim().equals(stopNumber)) {
                        if (cell != null && !cell.trim().isEmpty()) {
                            setter = clz.getDeclaredMethod(setterName(field.getName()), BigDecimal.class);
                            String numberStr = cell.replace(",", "").replace("%", "");
                            if (stopNumber == null || !numberStr.trim().equals(stopNumber)) {
                                try {
                                    setter.invoke(val, new BigDecimal(numberStr));
                                } catch (NumberFormatException e) {
                                    log.error("Failed to convert {} to number, row {}, {}, {}", numberStr, row, givenSymbol, givenDate);
                                    return null;
                                }
                            }
                        }
                    }
                } else if (field.getType().equals(Date.class)) {
                    if (stopNumber == null || !cell.trim().equals(stopNumber)) {
                        setter = clz.getDeclaredMethod(setterName(field.getName()), Date.class);
                        setter.invoke(val, sdf.parse(cell));
                    }
                } else {
                    setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                    setter.invoke(val, cell);
                }
                counter--;
            }
        }
        return val;
    }

    private static Object mapRowToBeanFromLeft(String[] row, Class clz, String dateFmt, boolean mandateDate,
                                               @Nullable String givenSymbol, @Nullable String givenDate,
                                               @Nullable String stopNumber) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFmt);
        Object val = clz.newInstance();
        Field[] fields = clz.getDeclaredFields();
        int counter = 0;
        for (Field field : fields) {
            Method setter = null;
            String cell = row[counter];
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                String symbol = null;
                if (givenSymbol != null) {
                    symbol = givenSymbol;
                } else {
                    symbol = cell;
                    counter++;
                }
                setter.invoke(val, symbol);
            } else if (field.getAnnotation(MySQLDateKey.class) != null) {
                Date date = null;
                if (givenDate != null) {
                    try {
                        date = sdf.parse(givenDate);
                    } catch (ParseException e) {
                        log.error("Failed to convert {} to date, row {}", givenDate, row);
                        if (mandateDate) {
                            return null;
                        }
                    }
                } else {
                    if (stopNumber == null || !cell.trim().equals(stopNumber)) {
                        try {
                            date = sdf.parse(cell);
                        } catch (ParseException e) {
                            if (mandateDate) {
                                return null;
                            } else {
                                counter++;
                                break;
                            }
                        }
                    } else {
                        return null;
                    }
                    counter++;
                }
                setter = clz.getDeclaredMethod(setterName(field.getName()), Date.class);
                setter.invoke(val, date);
            } else {
                if (field.getType().equals(BigDecimal.class)) {
                    if (stopNumber == null || !cell.trim().equals(stopNumber)) {
                        if (cell != null && !cell.trim().isEmpty()) {
                            setter = clz.getDeclaredMethod(setterName(field.getName()), BigDecimal.class);
                            String numberStr = cell.replace(",", "").replace("%", "");
                            if (stopNumber == null || !numberStr.trim().equals(stopNumber)) {
                                try {
                                    setter.invoke(val, new BigDecimal(numberStr));
                                } catch (NumberFormatException e) {
                                    log.error("Failed to convert {} to number, row {}, {}, {}", numberStr, row, givenSymbol, givenDate);
                                    return null;
                                }
                            }
                        }
                    }
                } else if (field.getType().equals(Date.class)) {
                    if (stopNumber == null || !cell.trim().equals(stopNumber)) {
                        setter = clz.getDeclaredMethod(setterName(field.getName()), Date.class);
                        setter.invoke(val, sdf.parse(cell));
                    }
                } else {
                    setter = clz.getDeclaredMethod(setterName(field.getName()), String.class);
                    setter.invoke(val, cell);
                }
                counter++;
            }
        }
        return val;
    }

    public static String setterName(String member) {
        char[] chars = member.toCharArray();
        chars[0] -= 32;
        return "set" + String.valueOf(chars);
    }
}
