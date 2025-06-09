package com.alphaentropy.web.eastmoney.loader.date;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class SBLDetailsLoader extends AbstractEastMoneyPerDateWebLoader {
    private static final String URL = "https://datacenter-web.eastmoney.com/api/data/v1/get?reportName=RPTA_WEB_RZRQ_GGMX&columns=ALL&pageNumber=#PG#&pageSize=1000&filter=(date%3D%27yyyy-mm-dd%27)";

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
        return "2010-04-01";
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }

    @Override
    protected String getSubDumpFolder() {
        return "sbl";
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
