package com.alphaentropy.domain.fundamental;

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
public class InsiderTradeRaw {
    //变动比例
    private BigDecimal changeRatio;

    private String insiderName1;

    @MySQLSymbolKey
    private String symbol;

    private String insiderName2;

    private String shareType;

    @MySQLDateKey
    private Date tradeDate;

    private BigDecimal changeShares;

    private BigDecimal heldShares;

    private BigDecimal tradePrice;

    private String stockName;

    private String relation;

    private String stockAddr;

    private String tradeType;

    private BigDecimal tradeAmt;

}
