package com.alphaentropy.web.eastmoney.loader.date;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class TSBoardSBLLoader extends AbstractEastMoneyPerDateWebLoader {
    private static final String URL = "https://datacenter-web.eastmoney.com/api/data/v1/get?pageSize=500&pageNumber=#PG#&columns=ALL&reportName=RPTA_WEB_KCBZLPSKCJ&quoteColumns=f2%2Cf3%2Cf124&filter=(tdate%3D%27yyyy-mm-dd%27)";

    @Override
    protected String getSubDumpFolder() {
        return "tsboard_sbl";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[] {"result", "data"};
    }

    @Override
    protected String getDay1() {
        return "2019-07-22";
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    protected int getExpiryHours() {
        // never expired
        return Integer.MAX_VALUE;
    }
}
