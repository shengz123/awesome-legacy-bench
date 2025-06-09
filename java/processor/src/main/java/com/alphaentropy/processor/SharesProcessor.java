package com.alphaentropy.processor;

import com.alphaentropy.common.utils.BeanUtil;
import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.common.utils.SymbolUtil;
import com.alphaentropy.domain.basic.*;
import com.alphaentropy.store.application.MySQLStatement;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.alphaentropy.common.utils.BeanUtil.*;

@Service
@Slf4j
public class SharesProcessor {

    @Autowired
    private MySQLStatement statement;

    private void processSymbol(String symbol, Map<String, Date> latestFromDest) {
        List limited = statement.queryBySymbol(LimitedShares.class, symbol, false);
        List outstanding = statement.queryBySymbol(OutstandingShares.class, symbol, false);
        List total = statement.queryBySymbol(TotalShares.class, symbol, false);
        Map<Date, BigDecimal> limitedMap = convertShareList(limited, LimitedShares.class);
        Map<Date, BigDecimal> outstandingMap = convertShareList(outstanding, OutstandingShares.class);
        Map<Date, BigDecimal> totalMap = convertShareList(total, TotalShares.class);
        Map<Date, BigDecimal> ratioMap = generateAShsRatio(limitedMap, outstandingMap, totalMap);

        List ratioList = getRatioList(symbol, ratioMap);
        List toAdd = LoadFilter.filter(ratioList, latestFromDest, AShareRatio.class);

        if (toAdd != null && !toAdd.isEmpty()) {
            statement.batchInsert(toAdd, AShareRatio.class);
        }
    }

    private List getRatioList(String symbol, Map<Date, BigDecimal> ratioMap) {
        List<AShareRatio> ret = new ArrayList<>();
        for (Date d : ratioMap.keySet()) {
            ret.add(new AShareRatio(symbol, d, ratioMap.get(d)));
        }
        return ret;
    }

    private Map<Date, BigDecimal> generateAShsRatio(Map<Date, BigDecimal> limitedMap,
                                                    Map<Date, BigDecimal> outstandingMap,
                                                    Map<Date, BigDecimal> totalMap) {
        Map<Date, BigDecimal> ret = new HashMap<>();
        for (Date d : totalMap.keySet()) {
            BigDecimal ratio = outstandingMap.get(d).add(limitedMap.get(d)).divide(totalMap.get(d),
                    5, BigDecimal.ROUND_HALF_UP);
            ret.put(d, ratio);
        }
        return ret;
    }

    private Map<Date, BigDecimal> convertShareList(List shares, Class clz) {
        Map<Date, BigDecimal> ret = new HashMap<>();
        for (Object obj : shares) {
            Date d = BeanUtil.getDate(obj, clz);
            BigDecimal v = (BigDecimal) BeanUtil.getFieldValue(clz, obj, "value");
            ret.put(d, v);
        }
        return ret;
    }

    public void process(List<String> symbols) throws Exception {
        Map<String, Date> latestFromDest = statement.getMostRecent(AShareRatio.class);
        ExecutorService symbolExecutorService = Executors.newFixedThreadPool(20);
        for (String symbol : symbols) {
            if (!SymbolUtil.isAShare(symbol)) {
                continue;
            }
            symbolExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        processSymbol(symbol, latestFromDest);
                    } catch (Exception e) {
                        log.error("Failed to process shares for {}", symbol, e);
                    }
                }
            });
        }
        symbolExecutorService.shutdown();
        symbolExecutorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS);
    }
}
