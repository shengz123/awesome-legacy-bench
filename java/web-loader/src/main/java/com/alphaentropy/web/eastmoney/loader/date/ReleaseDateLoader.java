package com.alphaentropy.web.eastmoney.loader.date;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class ReleaseDateLoader extends AbstractEastMoneyPerDateWebLoader {
    private static final String URL = "https://datacenter-web.eastmoney.com/api/data/v1/get?pageSize=1000&pageNumber=#PG#&reportName=RPT_PUBLIC_BS_APPOIN&columns=ALL&filter=(REPORT_DATE%3D%27yyyy-mm-dd%27)";

    @Override
    protected String getSubDumpFolder() {
        return "release_date";
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
        return "2006-12-31";
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return null;
    }
}
