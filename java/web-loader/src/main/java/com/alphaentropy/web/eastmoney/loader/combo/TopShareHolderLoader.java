package com.alphaentropy.web.eastmoney.loader.combo;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class TopShareHolderLoader extends AbstractEastMoneyPerSymbolDateWebLoader {
    private static final String URL = "http://f10.eastmoney.com/ShareholderResearch/PageSDGD?code=#MARKET#XXXXXX&date=yyyy-mm-dd";

    @Override
    protected String getSubDumpFolder() {
        return "top_share_holder";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[] { "sdgd" };
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
