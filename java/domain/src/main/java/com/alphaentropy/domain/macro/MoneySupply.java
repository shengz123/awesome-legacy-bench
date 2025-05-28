package com.alphaentropy.domain.macro;

import com.alphaentropy.domain.annotation.MySQLDateKey;
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
public class MoneySupply {
    @MySQLDateKey
    private Date month;

    private BigDecimal m2Amt;

    private BigDecimal m2Yoy;

    private BigDecimal m2Mom;

    private BigDecimal m1Amt;

    private BigDecimal m1Yoy;

    private BigDecimal m1Mom;

    private BigDecimal m0Amt;

    private BigDecimal m0Yoy;

    private BigDecimal m0Mom;
}
