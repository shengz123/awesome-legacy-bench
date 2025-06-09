package com.alphaentropy.web.eastmoney;

import com.alphaentropy.domain.basic.HolderSharesChange;
import com.alphaentropy.domain.fundamental.BuyBack;
import com.alphaentropy.domain.fundamental.InsiderTradeRaw;
import com.alphaentropy.domain.fundamental.SblRaw;
import com.alphaentropy.domain.fundamental.TsBoardSbl;
import com.alphaentropy.domain.offer.IssueOffer;
import com.alphaentropy.domain.offer.RightOffer;
import com.alphaentropy.web.eastmoney.loader.date.SBLDetailsLoader;
import com.alphaentropy.web.eastmoney.loader.date.TSBoardSBLLoader;
import com.alphaentropy.web.eastmoney.loader.direct.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EastMoneyDailyLoadDriver {
    @Autowired
    private EastMoneyLoadService loadService;

    @Autowired
    private SBLDetailsLoader sblDetailsLoader;

    @Autowired
    private TSBoardSBLLoader tsBoardSBLLoader;

    @Autowired
    private InsiderTradeLoader insiderTradeLoader;

    @Autowired
    private OfferIssueLoader  offerIssueLoader;

    @Autowired
    private OfferRightLoader offerRightLoader;

    @Autowired
    private BuyBackLoader buyBackLoader;

    @Autowired
    private HolderSharesChangeLoader holderSharesChangeLoader;

    public void routine(String date, List<String> symbols) {
        loadService.load(BuyBack.class, buyBackLoader);
        loadService.load(HolderSharesChange.class, holderSharesChangeLoader);
        loadService.load(InsiderTradeRaw.class, insiderTradeLoader);
        loadService.loadDate(date, SblRaw.class, sblDetailsLoader);
        loadService.loadDate(date, TsBoardSbl.class, tsBoardSBLLoader);
        loadService.load(IssueOffer.class, offerIssueLoader);
        loadService.load(RightOffer.class, offerRightLoader);
    }

    public void onDemandByDate(String date) {
        loadService.loadDate(date, SblRaw.class, sblDetailsLoader);
        loadService.loadDate(date, TsBoardSbl.class, tsBoardSBLLoader);
    }

    public void onDemandOneOff() {
        loadService.load(BuyBack.class, buyBackLoader);
        loadService.load(HolderSharesChange.class, holderSharesChangeLoader);
        loadService.load(InsiderTradeRaw.class, insiderTradeLoader);
        loadService.load(IssueOffer.class, offerIssueLoader);
        loadService.load(RightOffer.class, offerRightLoader);
    }

}
