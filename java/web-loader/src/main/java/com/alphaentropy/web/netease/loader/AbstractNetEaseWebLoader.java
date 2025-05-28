package com.alphaentropy.web.netease.loader;

import com.alphaentropy.web.common.CacheableWebLoader;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNetEaseWebLoader extends CacheableWebLoader {
    private static final String CHARSET = "gb2312";
    protected static final String CSV = "csv";
    protected static final String SYMBOL = "symbol";
    @Value("${netease_root}")
    protected String FOLDER_ROOT;
    protected static final String PLACEMENT = "XXXXXX";

    abstract protected String getSubDumpFolder();
    abstract protected String getUrlRoot();

    public String loadSymbol(String symbol) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(SYMBOL, symbol);
        return super.load(params, CHARSET, false, true);
    }

    protected String getDumpFolder() {
        return FOLDER_ROOT + getSubDumpFolder();
    }

    protected String url(Map<String, String> params) {
        String symbol = cacheKey(params);
        return getUrlRoot().replace(PLACEMENT, symbol);
    }

    protected String getDumpExt() {
        return CSV;
    }

    protected int getRetries() {
        return 2;
    }

    protected String cacheKey(Map<String, String> params) {
        return params.get(SYMBOL);
    }

    protected long waitTimeAfterLoad() {
        return 50L;
    }

    protected int getExpiryHours() {
        // 1 week
        return 24 * 7;
    }

}
