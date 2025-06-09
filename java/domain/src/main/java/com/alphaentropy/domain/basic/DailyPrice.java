package com.alphaentropy.domain.basic;

import com.alphaentropy.domain.annotation.*;
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
@CorpActionImpact
@Cached
//unadjusted daily prices
public class DailyPrice {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date day;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private BigDecimal amount;
}
