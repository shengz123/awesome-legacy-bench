package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class BalanceLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/zcfzb_XXXXXX.html";

    @Override
    protected String getSubDumpFolder() {
        return "balance";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

}
