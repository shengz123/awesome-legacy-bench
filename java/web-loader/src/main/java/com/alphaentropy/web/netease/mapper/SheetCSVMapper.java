package com.alphaentropy.web.netease.mapper;

import com.alphaentropy.common.utils.CSVDataFrame;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;

import static com.alphaentropy.common.utils.BeanUtil.mapDataFrameToBeans;

@Slf4j
public class SheetCSVMapper extends NetEaseCSVMapper {

    public SheetCSVMapper(Class clz, String content, String symbol) {
        this.entries = new ArrayList();
        CSVDataFrame df = CSVDataFrame.fromString(trimContent(content), DELIMITER);
        if (!df.isEmpty()) {
            this.entries = mapDataFrameToBeans(df, clz, YYYY_MM_DD,
                    1, 1, true, symbol, null, NA, false);
        }
    }
}
