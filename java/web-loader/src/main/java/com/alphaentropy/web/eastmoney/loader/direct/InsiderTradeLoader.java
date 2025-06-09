package com.alphaentropy.web.eastmoney.loader.direct;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class InsiderTradeLoader extends AbstractEastMoneyDirectWebLoader {
    private static final String URL = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=GG&sty=GGMX&p=1&ps=100000";

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
        return "insider";
    }
}
