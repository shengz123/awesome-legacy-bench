package com.alphaentropy.web.eastmoney;

import com.alphaentropy.domain.basic.FundHolder;
import com.alphaentropy.domain.basic.TopLiquidShareHolder;
import com.alphaentropy.domain.basic.TopShareHolder;
import com.alphaentropy.domain.fundamental.SharePledge;
import com.alphaentropy.domain.release.ReleaseDate;
import com.alphaentropy.domain.release.ReleaseDigest;
import com.alphaentropy.domain.release.ReleaseForecast;
import com.alphaentropy.web.eastmoney.loader.combo.TopFundShareHolderLoader;
import com.alphaentropy.web.eastmoney.loader.combo.TopLiquidShareHolderLoader;
import com.alphaentropy.web.eastmoney.loader.combo.TopShareHolderLoader;
import com.alphaentropy.web.eastmoney.loader.date.PledgeLoader;
import com.alphaentropy.web.eastmoney.loader.date.ReleaseDateLoader;
import com.alphaentropy.web.eastmoney.loader.date.ReleaseDigestLoader;
import com.alphaentropy.web.eastmoney.loader.date.ReleaseForecastLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EastMoneyQuarterlyLoadDriver {
    @Autowired
    private EastMoneyLoadService loadService;

    @Autowired
    private PledgeLoader pledgeLoader;

    @Autowired
    private ReleaseDateLoader releaseDateLoader;

    @Autowired
    private ReleaseDigestLoader releaseDigestLoader;

    @Autowired
    private ReleaseForecastLoader releaseForecastLoader;

    @Autowired
    private TopFundShareHolderLoader topFundShareHolderLoader;

    @Autowired
    private TopLiquidShareHolderLoader topLiquidShareHolderLoader;

    @Autowired
    private TopShareHolderLoader topShareHolderLoader;

    public void loadQuarter(String reportDate, List<String> symbols) {
        loadService.loadDate(reportDate, ReleaseDate.class, releaseDateLoader);
        loadService.loadDate(reportDate, ReleaseDigest.class, releaseDigestLoader);
        loadService.loadDate(reportDate, ReleaseForecast.class, releaseForecastLoader);
        loadService.loadDate(reportDate, SharePledge.class, pledgeLoader);

        loadService.loadSymbolDate(symbols, reportDate, FundHolder.class, topFundShareHolderLoader);
        loadService.loadSymbolDate(symbols, reportDate, TopLiquidShareHolder.class, topLiquidShareHolderLoader);
        loadService.loadSymbolDate(symbols, reportDate, TopShareHolder.class, topShareHolderLoader);
    }
}
