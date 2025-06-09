package com.alphaentropy.tdx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TdxLoadDriver {
    @Autowired
    private TdxLoadService loadService;

    public void routine() {
        loadService.loadReferences();
        loadService.loadDailyPrices();
    }

    public List<Date> getAllDatesSince(Date day1) {
        return loadService.getAllDatesSince(day1);
    }

    public List<String> getAllSymbols() {
        return loadService.getAllSymbols();
    }

    public List<String> getAllSymbolsFromReferences() {
        return loadService.getAllSymbolsFromReference();
    }
}
