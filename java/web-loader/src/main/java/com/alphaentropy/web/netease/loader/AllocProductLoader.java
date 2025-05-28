package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class AllocProductLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/gszl_XXXXXX.html?type=cp";

    @Override
    protected String getSubDumpFolder() {
        return "product";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }
}