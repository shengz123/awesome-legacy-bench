package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class CashFlowLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/xjllb_XXXXXX.html";

    @Override
    protected String getSubDumpFolder() {
        return "cashflow";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

}