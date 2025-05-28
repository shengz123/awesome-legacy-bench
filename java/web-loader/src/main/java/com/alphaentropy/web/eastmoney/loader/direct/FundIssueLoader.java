package com.alphaentropy.web.eastmoney.loader.direct;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.stereotype.Component;

@Component
public class FundIssueLoader extends AbstractEastMoneyDirectWebLoader {
    private static final String URL = "https://fund.eastmoney.com/data/FundNewIssue.aspx?t=xcln&sort=jzrgq&isbuy=2&page=1,50000";

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    protected String getSubDumpFolder() {
        return "fund";
    }

    @Override
    protected String[] getJsonKeys() {
        return new String[0];
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
                content = content.substring(content.indexOf("[")+1, content.lastIndexOf("]"));
                content = content.replace("\"", "");
                content = content.replace("[", "\"");
                content = content.replace("]", "\"");
                content = "[" + content + "]";
                return content;
            }
        };
    }
}
