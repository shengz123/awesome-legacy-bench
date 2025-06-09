package com.alphaentropy.web.eastmoney.loader;

import com.alphaentropy.web.eastmoney.mapper.EastMoneyJsonMapper;
import com.alphaentropy.web.eastmoney.mapper.EastMoneyPlainMapper;

public interface EastMoneyWebLoader {
    EastMoneyJsonMapper getJsonMapper(Class domainClz);
    EastMoneyPlainMapper getPlainMapper();
    String getDateFormat();
}
