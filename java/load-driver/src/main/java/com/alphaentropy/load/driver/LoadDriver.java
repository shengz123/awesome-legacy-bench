package com.alphaentropy.load.driver;

import com.alphaentropy.factor.infra.FactorCalDriver;
import com.alphaentropy.processor.BasicProcessorDriver;
import com.alphaentropy.tdx.TdxLoadDriver;
import com.alphaentropy.web.WebLoadDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class LoadDriver {
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);

    @Autowired
    private TdxLoadDriver tdxLoadDriver;

    @Autowired
    private WebLoadDriver webLoadDriver;

    @Autowired
    private BasicProcessorDriver basicProcessorDriver;

    @Autowired
    private FactorCalDriver factorCalDriver;

    @PostConstruct
    private void init() throws Exception {
        Date day1 = sdf.parse("2010-01-01");
        onDemandAllHistory(day1);
    }

    public void routine() {
        List<String> symbols = tdxLoadDriver.getAllSymbolsFromReferences();
        tdxLoadDriver.routine();
        webLoadDriver.loadDaily(symbols);
        basicProcessorDriver.routine(symbols);
    }

    public void onDemandAllHistory(Date day1) {
        List<String> symbols = tdxLoadDriver.getAllSymbolsFromReferences();
        tdxLoadDriver.routine();
        webLoadDriver.loadAllTime(symbols, day1);
        basicProcessorDriver.routine(symbols);
        // run only when factor module is stable
//        List<Date> allDates = tdxLoadDriver.getAllDatesSince(day1);
//        factorCalDriver.loadAllHistory(symbols, allDates);
    }

}
