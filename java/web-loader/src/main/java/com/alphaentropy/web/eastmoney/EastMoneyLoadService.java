package com.alphaentropy.web.eastmoney;

import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.web.eastmoney.loader.EastMoneyWebLoader;
import com.alphaentropy.web.eastmoney.loader.combo.AbstractEastMoneyPerSymbolDateWebLoader;
import com.alphaentropy.web.eastmoney.loader.date.AbstractEastMoneyPerDateWebLoader;
import com.alphaentropy.web.eastmoney.loader.direct.AbstractEastMoneyDirectWebLoader;
import com.alphaentropy.web.eastmoney.loader.symbol.AbstractEastMoneyPerSymbolWebLoader;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyJsonMapper;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class EastMoneyLoadService {
    @Autowired
    private MySQLStatement statement;

    private static final String DATE_FMT = "yyyy-MM-dd";

    public void loadSymbolDate(List<String> symbols, String date, Class clz,
                               AbstractEastMoneyPerSymbolDateWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(clz);
        for (String symbol : symbols) {
            loadSymbolDate(symbol, date, clz, webLoader, symbolDates);
        }
        log.info("Finish loading EastMoney {} for {}, took {} min", clz.getSimpleName(), date,
                stopwatch.elapsed(TimeUnit.MINUTES));
    }

    public void load(Class clz, AbstractEastMoneyDirectWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(clz);
        load(clz, webLoader, symbolDates);
        log.info("Finish loading EastMoney {}, took {} min", clz.getSimpleName(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    public void loadSingle(Class clz, AbstractEastMoneyDirectWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Date latest = statement.getLatest(clz);
        load(clz, webLoader, latest);
        log.info("Finish loading EastMoney {}, took {} min", clz.getSimpleName(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    public void loadSymbol(List<String> symbols, Class clz,
                           AbstractEastMoneyPerSymbolWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(clz);
        for (String symbol : symbols) {
            loadSymbol(symbol, clz, webLoader, symbolDates);
        }
        log.info("Finish loading EastMoney {}, took {} min", clz.getSimpleName(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    public void loadDate(String date, Class clz, AbstractEastMoneyPerDateWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(clz);
        loadDate(date, clz, webLoader, symbolDates);
        log.info("Finish loading EastMoney {} for {}, took {} min", clz.getSimpleName(), date,
                stopwatch.elapsed(TimeUnit.MINUTES));
    }

    private void load(Class clz, AbstractEastMoneyDirectWebLoader webLoader, Map<String, Date> symbolDates) {
        try {
            List ret = new ArrayList();
            if (webLoader.isPaged()) {
                int pageNo = 1;
                while (true) {
                    String content = webLoader.loadByPage(String.valueOf(pageNo));
                    List list = mapData(content, clz, webLoader);
                    if (list == null || list.isEmpty()) {
                        break;
                    } else {
                        ret.addAll(list);
                        pageNo++;
                    }
                }
            } else {
                String content = webLoader.load();
                ret = mapData(content, clz, webLoader);
            }
            List list = LoadFilter.filter(ret, symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load east money {}", clz.getSimpleName(), e);
        }
    }

    private void load(Class clz, AbstractEastMoneyDirectWebLoader webLoader, Date latest) {
        try {
            List ret = new ArrayList();
            if (webLoader.isPaged()) {
                int pageNo = 1;
                while (true) {
                    String content = webLoader.loadByPage(String.valueOf(pageNo));
                    List list = mapData(content, clz, webLoader);
                    if (list == null || list.isEmpty()) {
                        break;
                    } else {
                        ret.addAll(list);
                        pageNo++;
                    }
                }
            } else {
                String content = webLoader.load();
                ret = mapData(content, clz, webLoader);
            }
            List list = LoadFilter.filter(ret, latest, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load east money {}", clz.getSimpleName(), e);
        }
    }

    private void loadSymbolDate(String symbol, String date, Class clz, AbstractEastMoneyPerSymbolDateWebLoader webLoader,
                                Map<String, Date> symbolDates) {
        try {
            String newDate = LoadFilter.getEarliestDate(symbolDates, symbol, date, DATE_FMT);
            String content = webLoader.loadSymbolDate(symbol, newDate);
            List list = LoadFilter.filter(mapData(content, clz, webLoader), symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load east money {} for {} and {}", clz.getSimpleName(), symbol, date, e);
        }
    }

    private void loadSymbol(String symbol, Class clz, AbstractEastMoneyPerSymbolWebLoader webLoader,
                            Map<String, Date> symbolDates) {
        try {
            String content = webLoader.loadSymbol(symbol);
            List list = LoadFilter.filter(mapData(content, clz, webLoader), symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load east money {} for {}", clz.getSimpleName(), symbol, e);
        }
    }

    private void loadDate(String reportDate, Class clz, AbstractEastMoneyPerDateWebLoader webLoader,
                          Map<String, Date> symbolDates) {
        try {
            String newDate = LoadFilter.getEarliestDate(symbolDates, reportDate, DATE_FMT);
            log.info("Change date from {} to {}", reportDate, newDate);

            List ret = new ArrayList();
            if (webLoader.isPaged()) {
                int pageNo = 1;
                while (true) {
                    String content = webLoader.loadDateByPage(newDate, String.valueOf(pageNo));
                    List list = mapData(content, clz, webLoader);
                    if (list == null || list.isEmpty()) {
                        break;
                    } else {
                        ret.addAll(list);
                        pageNo++;
                    }
                }
            } else {
                String content = webLoader.loadDate(newDate);
                ret = mapData(content, clz, webLoader);
            }

            List list = LoadFilter.filter(ret, symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load east money {} for {}", clz.getSimpleName(), reportDate, e);
        }
    }

    private List mapData(String content, Class clz, EastMoneyWebLoader webLoader) {
        EastMoneyPlainMapper plainMapper = webLoader.getPlainMapper();
        if (plainMapper != null) {
            return plainMapper.map(content, clz);
        } else {
            EastMoneyJsonMapper jsonMapper = webLoader.getJsonMapper(clz);
            return jsonMapper.map(content, webLoader.getDateFormat());
        }
    }

}
