package com.alphaentropy.web.netease.mapper;

import com.alphaentropy.common.utils.CSVDataFrame;

import java.util.ArrayList;
import java.util.List;

import static com.alphaentropy.common.utils.BeanUtil.mapDataFrameToBeans;

public class IncomeAllocCSVMapper extends NetEaseCSVMapper {
    private static final int SPLITTER_LEN = 1;

    public IncomeAllocCSVMapper(Class clz, String content, String symbol) {
        entries = new ArrayList();
        CSVDataFrame allHistory = CSVDataFrame.fromString(content.trim(), DELIMITER);
        if (!allHistory.isEmpty()) {
            List<CSVDataFrame> byQuarter = allHistory.split(SPLITTER_LEN, new String[] {""});
            for (CSVDataFrame q : byQuarter) {
                String date = q.getCell(0, 4);
                CSVDataFrame df = q.sub(2, q.numRows(), 0, 20);
                List list = mapDataFrameToBeans(df, clz, YYYY_MM_DD, 0, 0, true, symbol, date, NA, true);
                if (list != null && !list.isEmpty()) {
                    entries.addAll(list);
                }
            }
        }
    }

}
