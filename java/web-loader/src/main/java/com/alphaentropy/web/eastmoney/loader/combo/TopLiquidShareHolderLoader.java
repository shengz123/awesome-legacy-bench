package com.alphaentropy.web.eastmoney.loader.combo;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class TopLiquidShareHolderLoader extends AbstractEastMoneyPerSymbolDateWebLoader {
    private static final String URL = "http://f10.eastmoney.com/ShareholderResearch/PageSDLTGD?code=#MARKET#XXXXXX&date=yyyy-mm-dd";

    @Override
    protected String getSubDumpFolder() {
        return "top_liq_share_holder";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[] { "sdltgd" };
    }

    @Override
    protected String getDay1() {
        return "2003-12-31";
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }
}
