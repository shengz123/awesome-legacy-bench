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
@Cached(frequency = "low")
//shall be applied to the raw price
//use to adjust the prices based on day1 price
public class ForwardAdjustFactor {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date effectDate;

    private BigDecimal factor;
}
