package com.alphaentropy.web.eastmoney.loader.date;

import com.alphaentropy.web.common.CacheableWebLoader;
import com.alphaentropy.web.eastmoney.loader.EastMoneyWebLoader;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyJsonMapper;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEastMoneyPerDateWebLoader extends CacheableWebLoader implements EastMoneyWebLoader {
    protected static final String CHARSET = "utf-8";
    protected static final String JSON = "json";
    protected static final String DATE = "date";
    protected static final String PAGE_NO = "pageNo";
    @Value("${eastmoney_date_root}")
    protected String FOLDER_ROOT;
    protected static final String PLACEMENT = "yyyy-mm-dd";
    protected static final String PLACEMENT_PG = "#PG#";
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
    abstract public boolean isPaged();

    public String loadDate(String date) throws Exception {
        if (needRun(date)) {
            Map<String, String> params = new HashMap<>();
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

    public String loadDateByPage(String date, String pageNum) throws Exception {
        if (needRun(date)) {
            Map<String, String> params = new HashMap<>();
            params.put(DATE, date);
            params.put(PAGE_NO, pageNum);
            return super.load(params, CHARSET, false, false);
        } else {
            return "";
        }
    }

    protected String getDumpFolder() {
        return FOLDER_ROOT + getSubDumpFolder();
    }

    @Override
    protected String url(Map<String, String> params) {
        String date = params.get(DATE);
        String pageNo = params.get(PAGE_NO);
        String url = getUrlRoot().replace(PLACEMENT, date);
        if (pageNo != null) {
            url = url.replace(PLACEMENT_PG, pageNo);
        }
        return url;
    }

    protected String getDumpExt() {
        return JSON;
    }

    protected int getRetries() {
        return 3;
    }

    protected String cacheKey(Map<String, String> params) {
        String date = params.get(DATE);
        String pageNo = params.get(PAGE_NO);
        if (pageNo != null) {
            return date + "_" + pageNo;
        } else {
            return date;
        }
    }

    protected long waitTimeAfterLoad() {
        return 200L;
    }

    protected int getExpiryHours() {
        // 1 week
        return 24 * 7;
    }

}