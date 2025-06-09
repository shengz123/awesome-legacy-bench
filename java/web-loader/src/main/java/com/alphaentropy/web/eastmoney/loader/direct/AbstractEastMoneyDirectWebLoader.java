package com.alphaentropy.web.eastmoney.loader.direct;

import com.alphaentropy.web.common.CacheableWebLoader;
import com.alphaentropy.web.eastmoney.loader.EastMoneyWebLoader;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyJsonMapper;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import com.alphaentropy.web.eastmoney.mapper.PayLoadFormat;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEastMoneyDirectWebLoader extends CacheableWebLoader implements EastMoneyWebLoader {
    protected static final String CHARSET = "utf-8";
    protected static final String JSON = "json";
    protected static final String PAGE_NO = "pageNo";

    @Value("${eastmoney_direct_root}")
    protected String FOLDER_ROOT;
    protected static final String PLACEMENT_PG = "#PG#";
    protected static final String DATE_FMT = "yyyy-MM-dd HH:mm:ss";

    abstract protected String getSubDumpFolder();
    abstract protected String getUrlRoot();
    abstract protected String[] getJsonKeys();
    abstract public boolean isPaged();

    public EastMoneyJsonMapper getJsonMapper(Class domainClz) {
        return new EastMoneyJsonMapper(domainClz, getJsonKeys(), DATE_FMT);
    }

    public String getDateFormat() {
        return null;
    }

    abstract public EastMoneyPlainMapper getPlainMapper();

    public String loadByPage(String pageNum) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(PAGE_NO, pageNum);
        return super.load(params, CHARSET, false, false);
    }

    public String load() throws Exception {
        return super.load(new HashMap<>(), CHARSET, false, false);
    }

    @Override
    protected String url(Map<String, String> params) {
        String pageNo = params.get(PAGE_NO);
        String url = getUrlRoot();
        if (pageNo != null) {
            url = url.replace(PLACEMENT_PG, pageNo);
        }
        return url;
    }

    protected String getDumpFolder() {
        return FOLDER_ROOT + getSubDumpFolder();
    }

    protected String getDumpExt() {
        return JSON;
    }

    protected int getRetries() {
        return 3;
    }

    protected String cacheKey(Map<String, String> params) {
        String name = getSubDumpFolder();
        if (params.containsKey(PAGE_NO)) {
            return name + "_" + params.get(PAGE_NO);
        }
        return name;
    }

    protected long waitTimeAfterLoad() {
        return 100L;
    }

    protected int getExpiryHours() {
        // 1 day
        return 48;
    }

}