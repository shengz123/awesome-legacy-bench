package com.alphaentropy.web.netease;

import com.alphaentropy.domain.fundamental.IncomeAllocByIndustry;
import com.alphaentropy.domain.fundamental.IncomeAllocByProduct;
import com.alphaentropy.domain.fundamental.IncomeAllocByRegion;
import com.alphaentropy.domain.fundamental.TurnoverRaw;
import com.alphaentropy.domain.sheet.BalanceRaw;
import com.alphaentropy.domain.sheet.CashFlowRaw;
import com.alphaentropy.domain.sheet.ProfitRaw;
import com.alphaentropy.web.netease.loader.*;
import com.alphaentropy.web.netease.mapper.NetEaseMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NetEaseLoadDriver {
    @Autowired
    private NetEaseLoadService loadService;

    @Autowired
    private BalanceLoader balanceLoader;

    @Autowired
    private CashFlowLoader cashFlowLoader;

    @Autowired
    private ProfitLoader profitLoader;

    @Autowired
    private TurnoverLoader turnoverLoader;

    @Autowired
    private AllocIndustryLoader industryLoader;

    @Autowired
    private AllocProductLoader productLoader;

    @Autowired
    private AllocRegionLoader regionLoader;

    public void loadQuarterly(List<String> symbols) {
        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.SHEET, symbols, BalanceRaw.class, balanceLoader);
        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.SHEET, symbols, CashFlowRaw.class, cashFlowLoader);
        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.SHEET, symbols, ProfitRaw.class, profitLoader);
        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.SHEET, symbols, TurnoverRaw.class, turnoverLoader);

        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.ALLOC, symbols, IncomeAllocByProduct.class, productLoader);
        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.ALLOC, symbols, IncomeAllocByIndustry.class, industryLoader);
        loadService.load(NetEaseMapperFactory.MAPPER_TYPE.ALLOC, symbols, IncomeAllocByRegion.class, regionLoader);
    }
}
