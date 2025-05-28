package com.alphaentropy.common.utils;

import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLVarchar;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

import static com.alphaentropy.common.utils.NameUtil.camelToUnix;

public class MySQLAnnotationUtil {

    private static final String DROP_TABLE = "drop table if exists $TABLE";
    private static final String DELETE_TABLE = "delete from $TABLE";
    private static final String CREATE_TABLE = "create table if not exists $TABLE ( $FIELDS )";
    private static final String CREATE_INDEX = "create index $TABLE_idx_$NUMBER on $TABLE ( $KEYS )";
    private static final String INSERT = "insert $TABLE ($FIELDS) values ( $VALUES )";
    private static final String CHECK_EXISTS = "select $KEYS from $TABLE";
    private static final String CHECK_MOST_RECENT = "select $SYMBOL, max($DATE) as $DATE from $TABLE group by $SYMBOL";
    private static final String CHECK_LATEST = "select max($DATE) as $DATE from $TABLE";
    private static final String QUERY_BY_DATE = "select * from $TABLE where $DATE = ?";
    private static final String QUERY_BY_SYMBOL_ORDER_DATE = "select * from $TABLE where $SYMBOL = ? ORDER BY $DATE";
    private static final String QUERY_BY_SYMBOL_DESC = "select * from $TABLE where $SYMBOL = ? ORDER BY $DATE desc";
    private static final String QUERY_BY_SYMBOL = "select * from $TABLE where $SYMBOL = ?";
    private static final String QUERY_SYMBOL_DATE = "select $SYMBOL, $DATE from $TABLE";
    private static final String QUERY_ALL = "select * from $TABLE";
    private static final String QUERY_SYMBOL_ALL_DATES_SINCE = "select distinct $DATE from $TABLE where $SYMBOL = ? and $DATE >= ? ORDER BY $DATE";
    private static final String QUERY_PAST = "select * from $TABLE where $SYMBOL = ? AND $DATE <= ? ORDER BY $DATE DESC LIMIT ?";
    private static final String QUERY_BY_SYMBOL_DATE = "select * from $TABLE where $SYMBOL = ? AND $DATE = ?";
    private static final String DELETE_BY_SYMBOL = "delete from $TABLE where $SYMBOL = ?";

    private static final String TABLE = "$TABLE";
    private static final String FIELDS = "$FIELDS";
    private static final String VALUES = "$VALUES";
    private static final String KEYS = "$KEYS";
    private static final String DATE = "$DATE";
    private static final String NO = "$NUMBER";
    private static final String SYMBOL = "$SYMBOL";
    private static final String LEN = "$LEN";
    private static final String QMK = "?";
    public static final String DEFAULT_VARCHAR_LEN = "20";

    private static Map<Class, String> defaultTypeMappings = new HashMap<>();

    static {
        defaultTypeMappings.put(BigDecimal.class, "numeric(30,5)");
        defaultTypeMappings.put(String.class, "varchar($LEN)");
        defaultTypeMappings.put(Date.class, "date");
    }

    public static String insertStatement(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer fieldSb = new StringBuffer();
        StringBuffer valueSb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            fieldSb.append(name);
            valueSb.append(QMK);
            if (i < fields.length - 1) {
                fieldSb.append(", ");
                valueSb.append(", ");
            }
        }
        return INSERT.replace(TABLE, tableName).replace(FIELDS, fieldSb.toString()).replace(VALUES, valueSb.toString());
    }

    public static String getMostRecentStatement(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
        }
        return CHECK_MOST_RECENT.replace(TABLE, tableName).replace(SYMBOL, symbol).replace(DATE, date);
    }

    public static String querySymbolDate(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        return QUERY_SYMBOL_DATE.replace(TABLE, tableName).replace(SYMBOL, symbol).replace(DATE, date);
    }

    public static String queryByDate(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        return QUERY_BY_DATE.replace(TABLE, tableName).replace(DATE, date);
    }

    public static String queryAll(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        return QUERY_ALL.replace(TABLE, tableName);
    }

    public static String queryBySymbolStatic(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
        }
        return QUERY_BY_SYMBOL.replace(TABLE, tableName).replace(SYMBOL, symbol);
    }

    public static String queryBySymbol(Class clz, boolean ascending) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        String sqlRoot = ascending ? QUERY_BY_SYMBOL_ORDER_DATE : QUERY_BY_SYMBOL_DESC;
        return sqlRoot.replace(TABLE, tableName).replace(SYMBOL, symbol).replace(DATE, date);
    }

    public static String queryPast(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        return QUERY_PAST.replace(TABLE, tableName).replace(SYMBOL, symbol).replace(DATE, date);
    }

    public static String queryAllDatesSince(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        return QUERY_SYMBOL_ALL_DATES_SINCE.replace(TABLE, tableName).replace(SYMBOL, symbol).replace(DATE, date);
    }

    public static String queryBySymbolDate(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        return QUERY_BY_SYMBOL_DATE.replace(TABLE, tableName).replace(SYMBOL, symbol).replace(DATE, date);
    }

    public static String deleteBySymbol(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
        }
        return DELETE_BY_SYMBOL.replace(TABLE, tableName).replace(SYMBOL, symbol);
    }

    public static String getLatest(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        Field[] fields = clz.getDeclaredFields();
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
        }
        return CHECK_LATEST.replace(TABLE, tableName).replace(DATE, date);
    }

    public static String getAllKeysStatement(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        List<String> keys = new ArrayList<>();
        for (Field field : fields) {
            String name = getColumnName(field.getName());
            if (isKey(field)) {
                keys.add(name);
            }
        }
        for (int i = 0; i < keys.size(); i++) {
            sb.append(keys.get(i));
            if (i < keys.size() - 1) {
                sb.append(", ");
            }
        }

        return CHECK_EXISTS.replace(TABLE, tableName).replace(KEYS, sb.toString());
    }

    public static String deleteFromTable(Class clz) {
        String tableName = getTableName(clz.getSimpleName());
        return DELETE_TABLE.replace(TABLE, tableName);
    }

    public static List<String> createTableStatements(Class clz, Map<String, String> typeMappings, boolean forceCleanup) {
        List<String> result = new ArrayList<>();
        String tableName = getTableName(clz.getSimpleName());
        if (forceCleanup) {
            result.add(DROP_TABLE.replace(TABLE, tableName));
        }
        StringBuffer sb = new StringBuffer();
        Field[] fields = clz.getDeclaredFields();
        String symbol = null;
        String date = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String name = getColumnName(field.getName());
            sb.append(name);
            sb.append(" ");
            sb.append(mapType(field));
            if (!isKey(field)) {
                sb.append(" null");
            }
            if (i < fields.length - 1) {
                sb.append(", ");
            }
            if (field.getAnnotation(MySQLDateKey.class) != null) {
                date = name;
            }
            if (field.getAnnotation(MySQLSymbolKey.class) != null) {
                symbol = name;
            }
        }
        result.add(CREATE_TABLE.replace(TABLE, tableName).replace(FIELDS, sb.toString()));
        if (date != null && symbol != null) {
            result.add(CREATE_INDEX.replace(TABLE, tableName).replace(NO, "1")
                    .replace(KEYS, date + ", " + symbol));
            result.add(CREATE_INDEX.replace(TABLE, tableName).replace(NO, "2")
                    .replace(KEYS, symbol + ", " + date));
        } else if (date != null) {
            result.add(CREATE_INDEX.replace(TABLE, tableName).replace(NO, "1")
                    .replace(KEYS, date));
        } else if (symbol != null) {
            result.add(CREATE_INDEX.replace(TABLE, tableName).replace(NO, "1")
                    .replace(KEYS, symbol));
        }
        return result;
    }

    private static String mapType(Field field) {
        Class type = field.getType();
        MySQLVarchar varchar = field.getAnnotation(MySQLVarchar.class);
        String ret = defaultTypeMappings.get(type);
        if (type.equals(String.class)) {
            if (varchar != null) {
                ret = ret.replace(LEN, varchar.value());
            } else {
                ret = ret.replace(LEN, DEFAULT_VARCHAR_LEN);
            }
        }
        return ret;
    }

    private static String getTableName(String clzName) {
        return camelToUnix(clzName);
    }

    private static String getColumnName(String fieldName) {
        return camelToUnix(fieldName);
    }

    private static boolean isKey(Field field) {
        return field.getAnnotation(MySQLDateKey.class) != null || field.getAnnotation(MySQLSymbolKey.class) != null;
    }
}
