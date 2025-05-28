package com.alphaentropy.web.eastmoney.loader.macro;

import com.alphaentropy.web.eastmoney.loader.direct.AbstractEastMoneyDirectWebLoader;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class PPILoader extends AbstractEastMoneyDirectWebLoader {
    private static final String URL = "https://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=GJZB&sty=ZGZB&p=1&ps=500&mkt=22";

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[0];
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

    @Override
    public boolean isPaged() {
        return false;
    }

    @Override
    protected String getSubDumpFolder() {
        return "cpi";
    }

    protected int getExpiryHours() {
        return 24*7;
    }
}
