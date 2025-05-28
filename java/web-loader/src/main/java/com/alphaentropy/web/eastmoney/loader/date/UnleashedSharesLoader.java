package com.alphaentropy.web.eastmoney.loader.date;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class UnleashedSharesLoader extends AbstractEastMoneyPerDateWebLoader {
    private static final String URL = "https://datacenter-web.eastmoney.com/api/data/v1/get?pageSize=1000&pageNumber=#PG#&reportName=RPT_LIFT_STAGE&columns=ALL&filter=(FREE_DATE%3E%3D%27yyyy-mm-dd%27)";

    @Override
    protected String getSubDumpFolder() {
        return "unleashed";
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
        return "2010-01-01";
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }

    @Override
    public boolean isPaged() {
        return true;
    }
}
