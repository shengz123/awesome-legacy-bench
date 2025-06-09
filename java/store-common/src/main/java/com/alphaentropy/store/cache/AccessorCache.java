package com.alphaentropy.store.cache;

import com.alphaentropy.common.utils.BeanUtil;
import com.alphaentropy.common.utils.DateTimeUtil;
import com.alphaentropy.domain.annotation.Factor;
import com.alphaentropy.domain.annotation.QuarterFactor;
import com.alphaentropy.domain.reference.Industry;
import com.alphaentropy.domain.reference.Region;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.store.infra.hbase.FactorBase;
import com.alphaentropy.store.infra.hbase.QuarterFactorBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AccessorCache implements DataAccessor {

    private static final String LOW_FREQ = "low";
    private static final String DAILY_FREQ = "daily";
    private static final String REF_FREQ = "ref";
    private static final String DATE_FMT = "yyyyMMdd";

    private boolean keepPastDataInCache = false;

    @Autowired
    private MySQLStatement statement;

    @Autowired
    private FactorBase factorBase;

    @Autowired
    private QuarterFactorBase quarterFactorBase;

    // cache for daily data
    private Map<Class, Map<String, Map<Date, Object>>> basicDateSeriesMap = new ConcurrentHashMap<>();

    // mapping from effective date to report date
    private Map<Class, Map<String, TreeMap<Date, Date>>> effectiveReportDates = new ConcurrentHashMap<>();

    // cache for low frequency data
    private Map<Class, Map<String, TreeMap<Date, Object>>> lowFrequentDateSeriesMap = new ConcurrentHashMap<>();

    // cache for past data
    private Map<Class, Map<String, TreeMap<Date, PastData>>> pastDateSeriesMap = new ConcurrentHashMap<>();

    private Map<Class, Map<String, Date>> noDBCache = new ConcurrentHashMap<>();

    private Map<Class, String> additionalReferenceConditions = new HashMap<>();

    @PostConstruct
    private void init() {
        preload();
        loadEffectiveDates();
        loadEffectiveDatesForQuarterFactor();
        loadReferenceCondition();
    }

    @Override
    public TreeMap<Date, Pair<Object, Object>> pair(String symbol, Class left, String leftAttr,
                                                    Class right, String rightAttr, Date date, int pastDays) {
        TreeMap<Date, Pair<Object, Object>> ret = new TreeMap<>();
        TreeMap<Date, Pair<Object, Object>> pair = pair(symbol, left, right, date, pastDays);
        for (Date d : pair.keySet()) {
            Pair<Object, Object> val = pair.get(d);
            ret.put(d, new Pair<>(BeanUtil.getFieldValue(left, val.getFirst(), leftAttr),
                    BeanUtil.getFieldValue(right, val.getSecond(), rightAttr)));
        }
        return ret;
    }

    @Override
    public Pair<Object, Object> pairPoint(String symbol, Class left, String leftAttr, Class right, String rightAttr,
                                          Date date, int offset) {
        int period = Math.abs(offset) + 1;
        TreeMap<Date, Object> rangeValues = past(symbol, left, date, period);
        if (!rangeValues.isEmpty()) {
            Object leftObj = rangeValues.firstEntry().getValue();
            Date d = rangeValues.firstEntry().getKey();
            Set<Date> dates = new HashSet<>();
            dates.add(d);
            TreeMap<Date, Object> rightTs = align(symbol, right, dates);
            if (rightTs != null && !rightTs.isEmpty()) {
                Object rightObj = rightTs.firstEntry().getValue();
                return new Pair<>(BeanUtil.getFieldValue(left, leftObj, leftAttr),
                        BeanUtil.getFieldValue(right, rightObj, rightAttr));
            }
        }
        return null;
    }

    @Override
    public void put(String symbol, Date date, String name, Object val) {
        factorBase.writeCell(symbol, date, name, val.toString());
    }

    @Override
    public void put(String symbol, Date date, Map<String, Object> multiValues, String frequency) {
        Map<String, String> toSave = new HashMap<>();
        multiValues.forEach((k, v) -> toSave.put(k, v.toString()));
        if (!toSave.isEmpty()) {
            if (frequency.equals("daily")) {
                factorBase.writeRow(symbol, date, toSave);
            } else if (frequency.equals("quarterly")) {
                quarterFactorBase.writeRow(symbol, date, toSave);
            }
        }
    }

    @Override
    public TreeMap<Date, Object> past(String symbol, Class clz, Date date, int pastDays) {
        Map<String, TreeMap<Date, PastData>> symbolDateSeries = null;
        if (keepPastDataInCache) {
            symbolDateSeries = pastDateSeriesMap.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
            if (symbolDateSeries != null) {
                TreeMap<Date, PastData> pastDataTreeMap = symbolDateSeries.get(symbol);
                if (pastDataTreeMap != null && pastDataTreeMap.containsKey(date)) {
                    PastData pastData = pastDataTreeMap.get(date);
                    if (pastData.period >= pastDays) {
                        List list = pastData.pastValues.subList(0, pastDays);
                        return BeanUtil.convertList(list, clz);
                    }
                }
            }
        }
        List list = statement.queryPast(clz, symbol, date, pastDays);
        if (keepPastDataInCache) {
            putPastDataIntoCache(list, symbol, clz);
            TreeMap<Date, PastData> dateSeries = symbolDateSeries.computeIfAbsent(symbol, v -> new TreeMap<>());
            PastData pastData = new PastData(list, pastDays);
            dateSeries.put(date, pastData);
        }
        return BeanUtil.convertList(list, clz);
    }

    @Override
    public TreeMap<Date, Object> past(String symbol, Class clz, String attr, Date date, int pastDays) {
        TreeMap<Date, Object> ret = new TreeMap<>();
        if (clz.equals(Factor.class)) {
            List<String> columns = new ArrayList<>();
            columns.add(attr);
            TreeMap<Date, Map<String, String>> factors = factorBase.scanPast(symbol, date, columns, pastDays);
            for (Date d : factors.keySet()) {
                String str = factors.get(d).get(attr);
                if (str != null) {
                    ret.put(d, new BigDecimal(str));
                }
            }
        } else {
            TreeMap<Date, Object> objMap = past(symbol, clz, date, pastDays);
            for (Date d : objMap.keySet()) {
                Object obj = objMap.get(d);
                if (obj != null) {
                    Object attrVal = BeanUtil.getFieldValue(clz, obj, attr);
                    if (attrVal != null) {
                        ret.put(d, attrVal);
                    }
                }
            }
        }
        return ret;
    }

    private void loadReferenceCondition() {
        this.additionalReferenceConditions.put(Industry.class, " AND catalog = 'TDXNHY'");
    }

    private TreeMap<Date, Pair<Object, Object>> pair(String symbol, Class left, Class right, Date date, int pastDays) {
        TreeMap<Date, Object> leftTs = past(symbol, left, date, pastDays);
        TreeMap<Date, Object> rightTs = align(symbol, right, leftTs.keySet());
        TreeMap<Date, Pair<Object, Object>> ret = new TreeMap<>();
        for (Date d : leftTs.keySet()) {
            ret.put(d, new Pair<>(leftTs.get(d), rightTs.get(d)));
        }
        return ret;
    }

    private Object point(String symbol, Class clz, Date date) {
        Object data = null;
        String frequency = BeanUtil.getCacheFrequency(clz);
        if (LOW_FREQ.equals(frequency)) {
            // check if it has effective date
            if (effectiveReportDates.containsKey(clz)) {
                date = getReportDateForLowFrequencyData(symbol, clz, date);
            }
            Pair<Date, Object> pair = pointLowFrequency(symbol, clz, date);
            if (pair != null) {
                data = pair.getValue();
            } else {
                if (data == null) {
                    data = hitDB(symbol, clz, date);
                    if (data != null) {
                        putLowFrequency(symbol, clz, date, data);
                    }
                }
            }
        } else if (DAILY_FREQ.equals(frequency)) {
            data = pointBasic(symbol, clz, date);
            if (data == null) {
                data = hitDB(symbol, clz, date);
                if (data != null) {
                    putBasicPoint(symbol, clz, date, data);
                }
            }
        } else if (REF_FREQ.equals(frequency)) {
            data = pointReference(symbol, clz, additionalReferenceConditions.get(clz));
        }
        return data;
    }

    @Override
    public Collection<String> getAllSymbols() {
        List<String> symbols = new ArrayList<>();
        List regions = statement.queryAll(Region.class);
        for (Object region : regions) {
            symbols.add(((Region) region).getSymbol());
        }
        return symbols;
    }

    @Override
    public Object point(String symbol, Class clz, String attr, Date date) {
        Object ret = null;
        if (clz.equals(Factor.class) || clz.equals(QuarterFactor.class)) {
            ret = getFactorVal(symbol, clz, attr, date);
        } else {
            Object obj = point(symbol, clz, date);
            if (obj != null) {
                ret = BeanUtil.getFieldValue(clz, obj, attr);
            }
        }
        return ret;
    }

    private Object getFactorVal(String symbol, Class clz, String attr, Date date) {
        String factorVal = null;
        if (clz.equals(Factor.class)) {
            factorVal = (String) pointFactor(symbol, date, attr);
        } else if (clz.equals(QuarterFactor.class)) {
            factorVal = (String) pointQuarterFactor(symbol, date, attr);
        }
        return factorVal != null ? new BigDecimal(factorVal) : null;
    }

    @Override
    public Object point(String symbol, Class clz, String attr, Date date, int offset) {
        int period = Math.abs(offset) + 1;
        TreeMap<Date, Object> rangeValues = past(symbol, clz, date, period);
        Object obj = rangeValues.firstEntry().getValue();
        if (obj != null) {
            return BeanUtil.getFieldValue(clz, obj, attr);
        }
        return null;
    }

    @Override
    public Pair<Date, Object> pointQ(String symbol, Class clz, String attr, Date date) {
        Pair<Date, Object> pair = pointLowFrequency(symbol, clz, date);
        if (pair != null) {
            Object attrVal = BeanUtil.getFieldValue(clz, pair.getValue(), attr);
            if (attrVal != null) {
                return new Pair<Date, Object>(pair.getKey(), attrVal);
            }
        }
        return null;
    }

    @Override
    public Pair<Date, Object> pointQ(String symbol, Class clz, String attr, Date date, int offset) {
        int loop = Math.abs(offset);
        for (int i = 0; i < loop; i++) {
            date = DateTimeUtil.getPrevQuarter(date);
        }
        Pair<Date, Object> pair = pointLowFrequency(symbol, clz, date);
        if (pair != null) {
            Object attrVal = BeanUtil.getFieldValue(clz, pair.getValue(), attr);
            if (attrVal != null) {
                return new Pair<Date, Object>(pair.getKey(), attrVal);
            }
        }
        return null;
    }

    private TreeMap<Date, Object> align(String symbol, Class clz, Collection<Date> dates) {
        TreeMap<Date, Object> ret = new TreeMap<>();
        String frequency = BeanUtil.getCacheFrequency(clz);
        if (LOW_FREQ.equals(frequency)) {
            for (Date date : dates) {
                Pair<Date, Object> pair = pointLowFrequency(symbol, clz, date);
                if (pair != null) {
                    ret.put(date, pair.getValue());
                }
            }
        } else if (DAILY_FREQ.equals(frequency)) {
            for (Date date : dates) {
                Object object = pointBasic(symbol, clz, date);
                if (object != null) {
                    ret.put(date, object);
                }
            }
        }
        return ret;
    }

    private void preload() {
        try {
            List<Class> classesToPreload = BeanUtil.getPreLoadClasses();
            for (Class clz : classesToPreload) {
                List all = statement.queryAll(clz);
                String frequency = BeanUtil.getFrequency(clz);
                if (LOW_FREQ.equals(frequency)) {
                    Map<String, TreeMap<Date, Object>> symbolDateMap
                            = lowFrequentDateSeriesMap.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
                    for (Object data : all) {
                        String symbol = BeanUtil.getSymbol(data, clz);
                        TreeMap<Date, Object> dateMap = symbolDateMap.computeIfAbsent(symbol, v -> new TreeMap<>());
                        Date date = BeanUtil.getDate(data, clz);
                        dateMap.putIfAbsent(date, data);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to preload", e);
        }
    }

    private void putPastDataIntoCache(List list, String symbol, Class clz) {
        String frequency = BeanUtil.getCacheFrequency(clz);
        if (LOW_FREQ.equals(frequency)) {
            Map<String, TreeMap<Date, Object>> symbolDatesMap
                    = lowFrequentDateSeriesMap.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
            TreeMap<Date, Object> dateSeries = symbolDatesMap.computeIfAbsent(symbol, v -> new TreeMap<>());
            for (Object object : list) {
                dateSeries.put(BeanUtil.getDate(object, clz), object);
            }
        } else if (DAILY_FREQ.equals(frequency)) {
            Map<String, Map<Date, Object>> data = basicDateSeriesMap.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
            Map<Date, Object> dateSeries = data.computeIfAbsent(symbol, v -> new TreeMap<>());
            for (Object object : list) {
                dateSeries.put(BeanUtil.getDate(object, clz), object);
            }
        }
    }

    private Object hitDB(String symbol, Class clz, Date date) {
        Map<String, Date> noDBSymbolDates = noDBCache.get(clz);
        if (noDBSymbolDates != null && noDBSymbolDates.get(symbol) != null) {
            if (date.equals(noDBSymbolDates.get(symbol))) {
                return null;
            }
        }

        Object data = statement.queryBySymbolDate(clz, symbol, date);
        if (data == null) {
            noDBSymbolDates = noDBCache.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
            noDBSymbolDates.put(symbol, date);
        }
        return data;
    }

    private void loadEffectiveDates() {
        try {
            Map<Class, Class> classesWithEffective = BeanUtil.getClassesWithEffectiveClz();
            Map<Class, Map<String, TreeMap<Date, Date>>> effectiveSymbolDates = new HashMap<>();
            for (Class clzWithEffect : classesWithEffective.keySet()) {
                Class effectClz = classesWithEffective.get(clzWithEffect);
                Map<String, TreeMap<Date, Date>> tmp = new HashMap<>();
                List effectReportDates = statement.queryAll(effectClz);
                for (Object obj : effectReportDates) {
                    String symbol = BeanUtil.getSymbol(obj, effectClz);
                    Date effectDate = (Date) BeanUtil.getFieldValue(effectClz, obj, BeanUtil.getEffectiveDateField(clzWithEffect));
                    Date reportDate = BeanUtil.getReportDate(obj, effectClz);
                    TreeMap<Date, Date> dateMapping = tmp.computeIfAbsent(symbol, v -> new TreeMap<>());
                    if (effectDate != null && reportDate != null) {
                        dateMapping.put(effectDate, reportDate);
                    }
                }
                effectiveSymbolDates.put(effectClz, tmp);
            }

            for (Class clz : classesWithEffective.keySet()) {
                effectiveReportDates.put(clz, effectiveSymbolDates.get(classesWithEffective.get(clz)));
            }
        } catch (Exception e) {
            log.error("Failed to load effective dates", e);
        }
    }

    private void loadEffectiveDatesForQuarterFactor() {
        Map<String, TreeMap<Date, Date>> val = effectiveReportDates.values().iterator().next();
        effectiveReportDates.put(QuarterFactor.class, val);
    }

    private Object pointBasic(String symbol, Class clz, Date date) {
        Map<String, Map<Date, Object>> symbolDateEntries = basicDateSeriesMap.get(clz);
        if (symbolDateEntries != null) {
            Map<Date, Object> dateEntries = symbolDateEntries.get(symbol);
            if (dateEntries != null) {
                return dateEntries.get(date);
            }
        }
        return null;
    }

    private Object pointReference(String symbol, Class clz, String additionalCond) {
        return statement.queryBySymbolStatic(clz, symbol, additionalCond);
    }

    private Object pointFactor(String symbol, Date date, String name) {
        return factorBase.readCell(symbol, date, name);
    }

    private Object pointQuarterFactor(String symbol, Date date, String name) {
        date = getReportDateForLowFrequencyData(symbol, QuarterFactor.class, date);
        return quarterFactorBase.readCell(symbol, date, name);
    }

    private Pair<Date, Object> pointLowFrequency(String symbol, Class clz, Date date) {
        if (date == null) {
            return null;
        }
        Map<String, TreeMap<Date, Object>> symbolDateEntries = lowFrequentDateSeriesMap.get(clz);
        if (symbolDateEntries != null) {
            TreeMap<Date, Object> dateEntries = symbolDateEntries.get(symbol);
            if (dateEntries != null) {
                if (dateEntries.floorEntry(date) != null) {
                    Map.Entry<Date, Object> entry = dateEntries.floorEntry(date);
                    return new Pair<Date, Object>(entry.getKey(), entry.getValue());
                }
            }
        } else {
            Object object = statement.queryBySymbolDate(clz, symbol, date);
            if (object != null) {
                Date d = BeanUtil.getDate(object, clz);
                return new Pair<Date, Object>(d, object);
            }
        }
        return null;
    }

    private Date getReportDateForLowFrequencyData(String symbol, Class clz, Date date) {
        Map<String, TreeMap<Date, Date>> symbolDatesMap = effectiveReportDates.get(clz);
        if (symbolDatesMap != null && symbolDatesMap.get(symbol) != null) {
            if (symbolDatesMap.get(symbol).floorEntry(date) != null) {
                return symbolDatesMap.get(symbol).floorEntry(date).getValue();
            }
        }
        return date;
    }

    private void putLowFrequency(String symbol, Class clz, Date date, Object value) {
        Map<String, TreeMap<Date, Object>> symbolDateEntries
                = lowFrequentDateSeriesMap.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
        Map<Date, Object> dateEntries = symbolDateEntries.computeIfAbsent(symbol, v -> new TreeMap<>());
        dateEntries.put(date, value);
    }

    public void putBasicPoint(String symbol, Class clz, Date date, Object value) {
        Map<String, Map<Date, Object>> symbolDateEntries = basicDateSeriesMap.computeIfAbsent(clz, v -> new ConcurrentHashMap<>());
        Map<Date, Object> dateEntries = symbolDateEntries.computeIfAbsent(symbol, v -> new ConcurrentHashMap<>());
        dateEntries.put(date, value);
    }

    @Data
    @AllArgsConstructor
    private class PastData {
        private List pastValues;
        private int period;
    }
}
