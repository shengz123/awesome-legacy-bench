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
public class Cpi {
    @MySQLDateKey
    private Date month;

    private BigDecimal current;

    private BigDecimal yoy;

    private BigDecimal mom;
}
