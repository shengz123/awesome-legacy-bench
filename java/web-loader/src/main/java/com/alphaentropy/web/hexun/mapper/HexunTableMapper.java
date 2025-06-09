package com.alphaentropy.web.hexun.mapper;

import com.alphaentropy.common.utils.CSVDataFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.alphaentropy.common.utils.BeanUtil.mapRowToBean;

@Slf4j
public abstract class HexunTableMapper {
    protected static final String YYYY_MM_DD = "yyyy-MM-dd";
    protected static final String NA = "-";

    abstract protected void cleanRow(String[] row);
    abstract protected boolean needFilter(Object row);
    abstract protected int skipRows();
    abstract protected int expectedCols();
    abstract protected String trim(String content);

    public List map(String content, Class clz, String symbol) {
        List entries = new ArrayList();
        content = trim(content);
        if (content == null) {
            return entries;
        }
        CSVDataFrame df = CSVDataFrame.fromHTML(content, skipRows(), expectedCols());
        for (int i = 0; i < df.numRows(); i++) {
            String[] row = df.getRow(i);
            cleanRow(row);
            try {
                Object value = mapRowToBean(row, clz, YYYY_MM_DD, true, symbol, null, NA, false);
                if (value != null && !needFilter(value)) {
                    entries.add(value);
                }
            } catch (Exception e) {
                log.error("Failed to create the mapper", e);
            }
        }
        return entries;
    }
}
