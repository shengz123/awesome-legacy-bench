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
@Cached(frequency = "low", preLoaded = true)
//shall be applied to the raw price
//use to adjust the prices based on today price
public class BackwardAdjustFactor {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date effectDate;

    @Factor(defaultVal = "1")
    private BigDecimal factor;
}
