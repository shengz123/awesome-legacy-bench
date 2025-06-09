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
public class UnleashedShareEvent {
    @MySQLSymbolKey
    private String securityCode;
    @MySQLDateKey
    private Date freeDate;

    //临近解禁股数
    private BigDecimal currentFreeShares;
    //实际可解禁股数
    private BigDecimal ableFreeShares;
    //解禁后流通股数
    private BigDecimal freeShares;
}
