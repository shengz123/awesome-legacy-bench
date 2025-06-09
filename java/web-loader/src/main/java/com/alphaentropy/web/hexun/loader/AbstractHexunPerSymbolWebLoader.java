package com.alphaentropy.web.hexun.loader;

import com.alphaentropy.web.common.CacheableWebLoader;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHexunPerSymbolWebLoader extends CacheableWebLoader {
    private static final String CHARSET = "gb2312";
    protected static final String TXT = "txt";
    protected static final String SYMBOL = "symbol";

    @Value("${hexun_root}")
    protected String FOLDER_ROOT;

    protected static final String PLACEMENT = "XXXXXX";

    abstract protected String getSubDumpFolder();
    abstract protected String getUrlRoot();

    public String loadSymbol(String symbol) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(SYMBOL, symbol);
        return load(params, CHARSET, false, false);
    }

    protected String getDumpFolder() {
        return FOLDER_ROOT + getSubDumpFolder();
    }

    protected String url(Map<String, String> params) {
        return getUrlRoot().replace(PLACEMENT, cacheKey(params));
    }

    protected String getDumpExt() {
        return TXT;
    }

    protected int getRetries() {
        return 5;
    }

    protected String cacheKey(Map<String, String> params) {
        return params.get(SYMBOL);
    }

    protected long waitTimeAfterLoad() {
        return 2000L;
    }

    protected int getExpiryHours() {
        // 1 week
        return 24 * 7;
    }

}