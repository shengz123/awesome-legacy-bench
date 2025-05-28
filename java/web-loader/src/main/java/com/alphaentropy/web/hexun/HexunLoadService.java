package com.alphaentropy.web.hexun;

import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.web.hexun.loader.AbstractHexunPerSymbolWebLoader;
import com.alphaentropy.web.hexun.mapper.HexunTableMapper;
import com.alphaentropy.web.hexun.mapper.HexunMapperFactory;
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
public class HexunLoadService {
    @Autowired
    private MySQLStatement statement;

    @Autowired
    private HexunMapperFactory mapperFactory;

    public void load(List<String> symbols, Class clz, AbstractHexunPerSymbolWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(clz);
        for (String symbol : symbols) {
            loadSymbol(symbol, clz, webLoader, symbolDates);
        }
        log.info("Finish loading Hexun {} took {} min", clz.getName(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    private void loadSymbol(String symbol, Class clz, AbstractHexunPerSymbolWebLoader webLoader,
                            Map<String, Date> symbolDates) {
        try {
            String content = webLoader.loadSymbol(symbol);
            HexunTableMapper mapper = mapperFactory.getMapper(clz);
            List list = LoadFilter.filter(mapper.map(content, clz, symbol), symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load hexun shares for " + symbol, e);
        }
    }
}
