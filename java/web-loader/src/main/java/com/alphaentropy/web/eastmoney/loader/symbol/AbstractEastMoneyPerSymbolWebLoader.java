package com.alphaentropy.web.eastmoney.loader.symbol;

import com.alphaentropy.web.common.CacheableWebLoader;
import com.alphaentropy.web.eastmoney.loader.EastMoneyWebLoader;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyJsonMapper;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEastMoneyPerSymbolWebLoader extends CacheableWebLoader implements EastMoneyWebLoader {
    protected static final String CHARSET = "utf-8";
    protected static final String JSON = "json";
    protected static final String SYMBOL = "symbol";
    protected static final String PAGE_NO = "pageNo";
    @Value("${eastmoney_symbol_root}")
    protected String FOLDER_ROOT;
    protected static final String PLACEMENT = "XXXXXX";
    protected static final String PLACEMENT_MARKET = "#MARKET#";
    protected static final String DATE_FMT = "yyyy-MM-dd HH:mm:ss";

    abstract protected String getSubDumpFolder();
    abstract protected String getUrlRoot();
    abstract protected String[] getJsonKeys();

    public String getDateFormat() {
        return null;
    }

    public EastMoneyJsonMapper getJsonMapper(Class domainClz) {
        return new EastMoneyJsonMapper(domainClz, getJsonKeys(), DATE_FMT);
    }

    abstract public EastMoneyPlainMapper getPlainMapper();

    public String loadSymbol(String symbol) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(SYMBOL, symbol);
        return super.load(params, CHARSET, false, false);
    }

    protected String getDumpFolder() {
        return FOLDER_ROOT + getSubDumpFolder();
    }

    protected String url(Map<String, String> params) {
        String symbol = params.get(SYMBOL);
        return getUrlRoot().replace(PLACEMENT, symbol).replace(PLACEMENT_MARKET, getMarket(symbol));
    }

    protected String getMarket(String symbol) {
        if (symbol.startsWith("6")) {
            return "SH";
        } else {
            return "SZ";
        }
    }

    protected String getDumpExt() {
        return JSON;
    }

    protected int getRetries() {
        return 3;
    }

    protected String cacheKey(Map<String, String> params) {
        String symbol = params.get(SYMBOL);
        String pageNo = params.get(PAGE_NO);
        if (pageNo != null) {
            return symbol + "_" + pageNo;
        } else {
            return symbol;
        }
    }

    protected long waitTimeAfterLoad() {
        return 100L;
    }

    protected int getExpiryHours() {
        // 1 week
        return 24 * 7;
    }

}
