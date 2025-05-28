package com.alphaentropy.web.netease.mapper;

import java.util.List;

public abstract class NetEaseCSVMapper {
    protected static final String YYYY_MM_DD = "yyyy-MM-dd";
    protected static final String NA = "--";
    protected static final String DELIMITER = ",";
    protected List entries;

    public List getData() {
        return entries;
    }

    protected String trimContent(String content) {
        return content.trim().replace("</tr>", "");
    }
}
