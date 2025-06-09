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
//raw adjustment factor, will be used to derive the cumulative adjust factor
public class RawAdjustFactor {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date effectDate;

    private BigDecimal factor;
}
