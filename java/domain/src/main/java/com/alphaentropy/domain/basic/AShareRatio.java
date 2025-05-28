package com.alphaentropy.domain.basic;

import com.alphaentropy.domain.annotation.Cached;
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
@Cached(frequency = "low", preLoaded = true)
public class AShareRatio {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date startDate;
    private BigDecimal value;
}
