package com.alphaentropy.web.netease.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NetEaseMapperFactory {
    public enum MAPPER_TYPE {
        SHEET,
        ALLOC
    };

    public NetEaseCSVMapper getCSVMapper(MAPPER_TYPE type, Class clz, String content, String symbol) {
        if (MAPPER_TYPE.SHEET.equals(type)) {
            return new SheetCSVMapper(clz, content, symbol);
        } else if (MAPPER_TYPE.ALLOC.equals(type)) {
            return new IncomeAllocCSVMapper(clz, content, symbol);
        }
        return null;
    }
}
