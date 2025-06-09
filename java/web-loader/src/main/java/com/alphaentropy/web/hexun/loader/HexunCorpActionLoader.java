package com.alphaentropy.web.hexun.loader;

import org.springframework.stereotype.Component;

@Component
public class HexunCorpActionLoader extends AbstractHexunPerSymbolWebLoader {

    private static final String URL = "http://stockdata.stock.hexun.com/2009_fhzzgb_XXXXXX.shtml";

    @Override
    protected String getSubDumpFolder() {
        return "corp_action";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }
}
