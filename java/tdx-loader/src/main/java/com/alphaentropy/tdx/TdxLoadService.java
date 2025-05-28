package com.alphaentropy.tdx;

import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.common.utils.SymbolUtil;
import com.alphaentropy.domain.basic.DailyPrice;
import com.alphaentropy.domain.reference.Industry;
import com.alphaentropy.domain.reference.Region;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.tdx.daily.BasicPriceLoader;
import com.alphaentropy.tdx.daily.IPOChecker;
import com.alphaentropy.tdx.daily.IndexDateLoader;
import com.alphaentropy.tdx.reference.IndustryLoader;
import com.alphaentropy.tdx.reference.RegionLoader;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TdxLoadService {
    @Value("${daily_file_path}")
    private String FOLDER;

    @Autowired
    private MySQLStatement statement;

    @Autowired
    private BasicPriceLoader basicPriceLoader;

    @Autowired
    private RegionLoader regionLoader;

    @Autowired
    private IndustryLoader industryLoader;

    @Autowired
    private IPOChecker ipoChecker;

    @Autowired
    private IndexDateLoader indexDateLoader;

    public List<String> getAllSymbols() {
        List<String> ret = new ArrayList<>();
        File fileRepoFolder = new File(FOLDER);
        File[] files = fileRepoFolder.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String symbol = fileName.substring(0, 6);
            if (SymbolUtil.isAShare(symbol)) {
                ret.add(symbol);
            }
        }
        return ret;
    }

    public List<Date> getAllDatesSince(Date day1) {
        return indexDateLoader.getAllDatesSince(day1);
    }

    public List<String> getAllSymbolsFromReference() {
        return regionLoader.getAllSymbols();
    }

    public void loadReferences() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        List regions = regionLoader.load();
        if (regions != null && !regions.isEmpty()) {
            statement.deleteTable(Region.class);
            statement.batchInsert(regions, Region.class);
        }

        List industries = industryLoader.load();
        if (industries != null && !industries.isEmpty()) {
            statement.deleteTable(Industry.class);
            statement.batchInsert(industries, Industry.class);
        }
        log.info("Finish loading TDX references, took {} min", stopwatch.elapsed(TimeUnit.MINUTES));
    }

    public void loadDailyPrices() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ipoChecker.checkIPODates();

        ExecutorService symbolExecutorService = Executors.newFixedThreadPool(20,
                new ThreadFactoryBuilder().setNameFormat("DailySymbolLoader-%d").build());
        Map<String, Date> latestDailyPrices = statement.getMostRecent(DailyPrice.class);

        File fileRepoFolder = new File(FOLDER);
        File[] files = fileRepoFolder.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String symbol = fileName.substring(0, 6);
            if (fileName.endsWith(".txt") && fileName.length() == 10) {
                symbolExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        List all = basicPriceLoader.loadSymbolDaily(symbol, file);
                        List filtered = LoadFilter.filter(all, latestDailyPrices, DailyPrice.class);
                        if (filtered != null && !filtered.isEmpty()) {
                            statement.batchInsert(filtered, DailyPrice.class);
                        }
                        log.info("Processed daily prices of {}", symbol);
                    }
                });
            }
        }

        symbolExecutorService.shutdown();
        try {
            symbolExecutorService.awaitTermination(7, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("Failed to shutdown", e);
        }

        log.info("Finish loading TDX daily prices, took {} min", stopwatch.elapsed(TimeUnit.MINUTES));
    }

}
