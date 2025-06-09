package com.alphaentropy.web.eastmoney.loader.date;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class ReleaseDigestLoader extends AbstractEastMoneyPerDateWebLoader {

    private static final String URL = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=SR&sty=YJKB&fd=yyyy-mm-dd&ps=200000";

    @Override
    protected String getSubDumpFolder() {
        return "digest";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[0];
    }

    @Override
    protected String getDay1() {
        return "2005-12-31";
    }

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    public EastMoneyPlainMapper getPlainMapper() {
        return new EastMoneyPlainMapper() {
            @Override
            public String trimContent(String content) {
                content = content.substring(content.indexOf("["), content.lastIndexOf("]"));
                return content + "]";
            }
        };
    }
}
