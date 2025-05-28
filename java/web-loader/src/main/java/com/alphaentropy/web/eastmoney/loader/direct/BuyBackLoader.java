package com.alphaentropy.web.eastmoney.loader.direct;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class BuyBackLoader extends AbstractEastMoneyDirectWebLoader {
    private static final String URL = "https://datacenter-web.eastmoney.com/api/data/v1/get?pageSize=500&pageNumber=#PG#&reportName=RPTA_WEB_GETHGLIST&columns=ALL";

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[] {"result", "data"};
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }

    @Override
    public String getDateFormat() {
        return "yyyy/MM/dd";
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    protected String getSubDumpFolder() {
        return "buy_back";
    }
}