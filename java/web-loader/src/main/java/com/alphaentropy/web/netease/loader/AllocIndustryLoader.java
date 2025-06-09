package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class AllocIndustryLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/gszl_XXXXXX.html?type=hy";

    @Override
    protected String getSubDumpFolder() {
        return "industry";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

}