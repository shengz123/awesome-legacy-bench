package com.alphaentropy.web.hexun.loader;

import org.springframework.stereotype.Component;

@Component
public class SharesChangeLoader extends AbstractHexunPerSymbolWebLoader {

    private static final String URL = "http://stockdata.stock.hexun.com/2009_gbjg_XXXXXX.shtml";
    private static final String START_STRING = "<div class=\"add3\">变动原因";

    @Override
    protected String getSubDumpFolder() {
        return "shares_change";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

    @Override
    public String loadSymbol(String symbol) throws Exception {
        String content = super.loadSymbol(symbol);
        int startIdx = content.indexOf(START_STRING);
        if (startIdx < 0) {
            return null;
        }
        content = content.substring(startIdx);
        startIdx = content.indexOf("<table");
        int endIdx = content.indexOf("</table>");
        content = content.substring(startIdx, endIdx + 8);
        return content;
    }
}
