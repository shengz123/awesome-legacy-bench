package com.alphaentropy.tdx.daily;

import com.alphaentropy.common.utils.CSVDataFrame;
import com.alphaentropy.domain.basic.DailyPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

import static com.alphaentropy.common.utils.BeanUtil.mapDataFrameToBeans;

@Component
@Slf4j
public class BasicPriceLoader {
    private static final int NUM_COLS = 7;
    private static final int NUM_HEADERS = 2;
    private static final String DELIMITER = "\t";

    public List loadSymbolDaily(String symbol, File dailyFile) {
        CSVDataFrame df = CSVDataFrame.fromFile(dailyFile, DELIMITER, NUM_HEADERS, NUM_COLS);
        return mapDataFrameToBeans(df, DailyPrice.class, "MM/dd/yyyy",
                0, 0, true, symbol, null, null, false);
    }
}
