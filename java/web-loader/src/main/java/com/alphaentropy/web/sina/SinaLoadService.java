package com.alphaentropy.web.sina;

import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.web.sina.loader.AbstractSinaPerSymbolWebLoader;
import com.alphaentropy.web.sina.mapper.SinaShareMapper;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SinaLoadService {

    @Autowired
    private MySQLStatement statement;

    public void load(List<String> symbols, Class clz, AbstractSinaPerSymbolWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(clz);
        for (String symbol : symbols) {
            loadSymbol(symbol, clz, webLoader, symbolDates);
        }
        log.info("Finish loading sina {} took {} min", clz.getName(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    private void loadSymbol(String symbol, Class clz, AbstractSinaPerSymbolWebLoader webLoader,
                            Map<String, Date> symbolDates) {
        try {
            String content = webLoader.loadSymbol(symbol);
            List all = new SinaShareMapper().map(content, clz, symbol);
            List list = LoadFilter.filter(all, symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load sina shares for " + symbol, e);
        }
    }
}
