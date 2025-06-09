package com.alphaentropy.web.hexun.mapper;

import com.alphaentropy.domain.basic.SharesChangeEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SharesChangeMapper extends HexunTableMapper {
    private static final int SKIP_ROWS = 4;
    private static final int EXPECTED_CNT = 4;

    @Override
    protected void cleanRow(String[] row) {
        String reason = row[3];
        row[3] = reason.substring(reason.indexOf(">") + 1, reason.lastIndexOf("<")).trim();
    }

    @Override
    protected boolean needFilter(Object row) {
        SharesChangeEvent event = (SharesChangeEvent) row;
        if (event.getTotalSharesBefore() == null
                || event.getTotalSharesAfter().compareTo(event.getTotalSharesBefore()) != 0) {
            return false;
        }
        return true;
    }

    @Override
    protected int skipRows() {
        return SKIP_ROWS;
    }

    @Override
    protected int expectedCols() {
        return EXPECTED_CNT;
    }

    @Override
    protected String trim(String content) {
        return null;
    }

}
