package com.alphaentropy.web.netease;

import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.web.netease.loader.AbstractNetEaseWebLoader;
import com.alphaentropy.web.netease.mapper.NetEaseMapperFactory;
import com.alphaentropy.web.netease.mapper.NetEaseCSVMapper;
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
public class NetEaseLoadService {

    @Autowired
    private MySQLStatement statement;

    @Autowired
    private NetEaseMapperFactory mapperFactory;

    public void load(NetEaseMapperFactory.MAPPER_TYPE mapperType, List<String> symbols, Class sheetClz, AbstractNetEaseWebLoader webLoader) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Date> symbolDates = statement.getMostRecent(sheetClz);
        for (String symbol : symbols) {
            loadSymbol(mapperType, symbol, sheetClz, webLoader, symbolDates);
        }
        log.info("Finish loading NetEase {}, took {} min", sheetClz.getName(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    private void loadSymbol(NetEaseMapperFactory.MAPPER_TYPE mapperType, String symbol, Class clz,
                            AbstractNetEaseWebLoader webLoader, Map<String, Date> symbolDates) {
        try {
            String content = webLoader.loadSymbol(symbol);
            NetEaseCSVMapper mapper = mapperFactory.getCSVMapper(mapperType, clz, content, symbol);
            List list = LoadFilter.filter(mapper.getData(), symbolDates, clz);
            if (list != null && !list.isEmpty()) {
                statement.batchInsert(list, clz);
            }
        } catch (Exception e) {
            log.error("Failed to load sheet for " + symbol, e);
        }
    }
}
