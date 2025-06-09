package com.alphaentropy.web.eastmoney.loader.combo;

import com.alphaentropy.web.common.CacheableWebLoader;
import com.alphaentropy.web.eastmoney.loader.EastMoneyWebLoader;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyJsonMapper;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEastMoneyPerSymbolDateWebLoader extends CacheableWebLoader implements EastMoneyWebLoader {
    protected static final String CHARSET = "utf-8";
    protected static final String JSON = "json";
    protected static final String DATE = "date";
    protected static final String SYMBOL = "symbol";

    @Value("${eastmoney_date_symbol_root}")
    protected String FOLDER_ROOT;

    protected static final String PLACEMENT_DATE = "yyyy-mm-dd";
    protected static final String PLACEMENT_SYMBOL = "XXXXXX";
    protected static final String PLACEMENT_MARKET = "#MARKET#";
    protected static final String DATE_FMT = "yyyy-MM-dd HH:mm:ss";

    abstract protected String getSubDumpFolder();
    abstract protected String getUrlRoot();
    abstract protected String[] getJsonKeys();
    abstract protected String getDay1();

    public String getDateFormat() {
        return null;
    }

    public EastMoneyJsonMapper getJsonMapper(Class domainClz) {
        return new EastMoneyJsonMapper(domainClz, getJsonKeys(), DATE_FMT);
    }

    abstract public EastMoneyPlainMapper getPlainMapper();

    public String loadSymbolDate(String symbol, String date) throws Exception {
        if (needRun(date)) {
            Map<String, String> params = new HashMap<>();
            params.put(SYMBOL, symbol);
            params.put(DATE, date);
            return super.load(params, CHARSET, false, false);
        } else {
            return "";
        }
    }

    protected boolean needRun(String date) {
        String day1 = getDay1();
        if (day1 != null) {
            int day1Val = Integer.parseInt(day1.replace("-", ""));
            int dateVal = Integer.parseInt(date.replace("-", ""));
            if (dateVal < day1Val) {
                return false;
            }
        }
        return true;
    }

    protected String getDumpFolder() {
        return FOLDER_ROOT + getSubDumpFolder();
    }

    protected String url(Map<String, String> params) {
        String date = params.get(DATE);
        String symbol = params.get(SYMBOL);
        return getUrlRoot().replace(PLACEMENT_DATE, date).replace(PLACEMENT_SYMBOL, symbol).replace(PLACEMENT_MARKET, getMarket(symbol));
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
        String date = params.get(DATE);
        String symbol = params.get(SYMBOL);
        return symbol + "_" + date;
    }

    protected long waitTimeAfterLoad() {
        return 100L;
    }

    protected int getExpiryHours() {
        // 1 month
        return 24 * 30;
    }

}