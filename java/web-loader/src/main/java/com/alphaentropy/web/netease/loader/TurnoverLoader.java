package com.alphaentropy.web.netease.loader;

import org.springframework.stereotype.Component;

@Component
public class TurnoverLoader extends AbstractNetEaseWebLoader {
    private static final String URL = "http://quotes.money.163.com/service/zycwzb_XXXXXX.html?type=report&part=yynl";

    @Override
    protected String getSubDumpFolder() {
        return "turnover";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }

}