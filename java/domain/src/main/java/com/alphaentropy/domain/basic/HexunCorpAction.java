package com.alphaentropy.domain.basic;

import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@Setter
@Getter
@MySQLTable
@NoArgsConstructor
@AllArgsConstructor
public class HexunCorpAction {
    @MySQLSymbolKey
    private String symbol;
    //公告日
    private Date eventDate;
    //分红
    private BigDecimal cashDividend;
    //送股
    private BigDecimal stockDividend;
    //转股
    private BigDecimal stockSplit;
    //登记日
    private Date rightDate;
    //派现额度(万元)
    private BigDecimal totalCashDividend;

    //除权日
    @MySQLDateKey
    private Date effectiveDate;
}
