package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class AllocRegionLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/gszl_XXXXXX.html?type=dy";

    @Override
    protected String getSubDumpFolder() {
        return "region";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }
}