package com.alphaentropy.web.eastmoney;

import com.alphaentropy.domain.macro.Cpi;
import com.alphaentropy.domain.macro.MoneySupply;
import com.alphaentropy.domain.macro.Ppi;
import com.alphaentropy.domain.offer.ConvertibleOffer;
import com.alphaentropy.domain.offer.FundIssue;
import com.alphaentropy.web.eastmoney.loader.direct.FundIssueLoader;
import com.alphaentropy.web.eastmoney.loader.direct.OfferConvertibleLoader;
import com.alphaentropy.web.eastmoney.loader.macro.CPILoader;
import com.alphaentropy.web.eastmoney.loader.macro.MoneySupplyLoader;
import com.alphaentropy.web.eastmoney.loader.macro.PPILoader;
import com.alphaentropy.web.eastmoney.loader.symbol.CorpActionLoader;
import com.alphaentropy.web.eastmoney.loader.symbol.OutstandingShsChangeLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EastMoneyWeeklyLoadDriver {
    @Autowired
    private EastMoneyLoadService loadService;

    @Autowired
    private FundIssueLoader fundIssueLoader;

    @Autowired
    private OfferConvertibleLoader offerConvertibleLoader;

//    @Autowired
//    private OutstandingShsChangeLoader outstandingShsChangeLoader;

//    @Autowired
//    private CorpActionLoader corpActionLoader;

    @Autowired
    private CPILoader cpiLoader;

    @Autowired
    private PPILoader ppiLoader;

    @Autowired
    private MoneySupplyLoader moneySupplyLoader;

    public void routine(String date, List<String> symbols) {
        onDemandOneOff(symbols);
    }

    public void onDemandOneOff(List<String> symbols) {
        loadService.load(FundIssue.class, fundIssueLoader);
        loadService.load(ConvertibleOffer.class, offerConvertibleLoader);
//        loadService.loadSymbol(symbols, OutstandingSharesChange.class, outstandingShsChangeLoader);
//        loadService.loadSymbol(symbols, CorpAction.class, corpActionLoader);

        loadService.loadSingle(Cpi.class, cpiLoader);
        loadService.loadSingle(Ppi.class, ppiLoader);
        loadService.loadSingle(MoneySupply.class, moneySupplyLoader);
    }
}
