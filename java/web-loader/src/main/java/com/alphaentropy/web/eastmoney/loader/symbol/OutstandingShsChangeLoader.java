package com.alphaentropy.web.eastmoney.loader.symbol;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

//@Component
@Deprecated
public class OutstandingShsChangeLoader extends AbstractEastMoneyPerSymbolWebLoader {
    private static final String URL = "http://f10.eastmoney.com/CapitalStockStructure/PageAjax?code=#MARKET#XXXXXX";

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[] {"lngbbd"};
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }

    @Override
    protected String getSubDumpFolder() {
        return "outstanding_shs";
    }

}
