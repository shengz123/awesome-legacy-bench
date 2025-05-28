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
// shall be applied to the unadjusted price
public class OutstandingShares {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date startDate;
    //万股
    private BigDecimal value;
}
