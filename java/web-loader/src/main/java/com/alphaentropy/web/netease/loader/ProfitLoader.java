package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class ProfitLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/lrb_XXXXXX.html";

    @Override
    protected String getSubDumpFolder() {
        return "profit";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

}