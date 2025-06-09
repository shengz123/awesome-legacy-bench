package com.alphaentropy.query.application;

import com.alphaentropy.common.utils.BeanUtil;
import com.alphaentropy.common.utils.SpelUtil;
import com.alphaentropy.domain.annotation.Factor;
import com.alphaentropy.domain.annotation.MySQLTable;
import com.alphaentropy.store.cache.DataAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.alphaentropy.common.utils.DateTimeUtil.getDatesBetween;

@Slf4j
@Service
public class QueryService {
    @Autowired
    private DataAccessor accessor;

    @PostConstruct
    private void init() throws Exception {
        //testQuery();
    }

    private void testQuery() throws Exception {
        List<String> fields = new ArrayList<>();
        fields.add("totCap");
        fields.add("osCap");
        fields.add("dPnl");
        fields.add("DailyPrice.close");
        TreeMap<Date, Map<String, Object>> ret = query("600290", new SimpleDateFormat("yyyy-MM-dd").parse("2022-01-01"),
                new SimpleDateFormat("yyyy-MM-dd").parse("2022-06-01"), fields);
        log.info(ret.toString());
    }

    private void testSearch() throws Exception {
        Date valueDate = new SimpleDateFormat("yyyy-MM-dd").parse("2022-06-01");
        String condition = "#osCap < 2000000 && #totCap < 2000000 && #cash2TotCap > 0.7 && #Industry.level1Code != 'T10' && #ProfitRaw.fundingExpense < 0 && #AShareRatio.value > 0.9";
        List<String> fields = new ArrayList<>();
        fields.add("Industry.level1Name");
        fields.add("Industry.level2Name");
        Map<String, Map<String, Object>> ret = search(valueDate, condition, fields);
        log.info(ret.toString());
    }

    public TreeMap<Date, Map<String, Object>> query(String symbol, Date start, Date end,
                                                    List<String> returnedColumns) {
        TreeMap<Date, Map<String, Object>> ret = new TreeMap<>();
        List<Date> dates = getDatesBetween(start, end);

        for (Date valueDate : dates) {
            Map<String, Object> values = new HashMap<>();
            for (String field : returnedColumns) {
                Object val = accessor.point(symbol, findClassFromVariable(field),
                        findColumnFromVariable(field), valueDate);
                if (val != null) {
                    values.put(field, val);
                }
            }
            if (!values.isEmpty()) {
                ret.put(valueDate, values);
            }
        }
        return ret;
    }

    private List<Class> resolveClasses(List<String> returnedColumns) {
        List<Class> ret = new ArrayList<>();
        for (String column : returnedColumns) {
            Class clz = findClassFromVariable(column);
            ret.add(clz);
        }
        return ret;
    }

    public Map<String, Map<String, Object>> search(Date valueDate, String conditionExpr, List<String> returnedColumns) {
        Map<String, Map<String, Object>> ret = new HashMap<>();
        Set<String> fields = new HashSet<>();
        fields.addAll(SpelUtil.parseVariables(conditionExpr));
        fields.addAll(returnedColumns);
        Collection<String> symbols = accessor.getAllSymbols();
        for (String symbol : symbols) {
            Map perSymbolCtx = new HashMap();
            for (String field : fields) {
                Object val = accessor.point(symbol, findClassFromVariable(field),
                        findColumnFromVariable(field), valueDate);
                perSymbolCtx.put(field, val);
            }
            Boolean matched = SpelUtil.valueOfBooleanExpr(perSymbolCtx, conditionExpr);
            if (matched) {
                ret.put(symbol, perSymbolCtx);
            }
        }
        return ret;
    }

    private Class findClassFromVariable(String variable) {
        if (variable.contains(".")) {
            String clzName = variable.split("\\.")[0];
            return BeanUtil.findClassWithAnnotation(MySQLTable.class, clzName);
        } else {
            return Factor.class;
        }
    }

    private String findColumnFromVariable(String variable) {
        if (variable.contains(".")) {
            return variable.split("\\.")[1];
        } else {
            return variable;
        }
    }
}
