package com.alphaentropy.web.sina;

import com.alphaentropy.domain.basic.HexunCorpAction;
import com.alphaentropy.domain.basic.LimitedShares;
import com.alphaentropy.domain.basic.OutstandingShares;
import com.alphaentropy.domain.basic.TotalShares;
import com.alphaentropy.web.sina.loader.SinaLimitedSharesLoader;
import com.alphaentropy.web.sina.loader.SinaOutstandingSharesLoader;
import com.alphaentropy.web.sina.loader.SinaTotalSharesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class SinaLoadDriver {
    @Autowired
    private SinaLimitedSharesLoader sinaLimitedSharesLoader;

    @Autowired
    private SinaOutstandingSharesLoader sinaOutstandingSharesLoader;

    @Autowired
    private SinaTotalSharesLoader sinaTotalSharesLoader;

    @Autowired
    private SinaLoadService loadService;

    public void load(List<String> symbols) {
        loadService.load(symbols, LimitedShares.class, sinaLimitedSharesLoader);
        loadService.load(symbols, OutstandingShares.class, sinaOutstandingSharesLoader);
        loadService.load(symbols, TotalShares.class, sinaTotalSharesLoader);
    }

}
