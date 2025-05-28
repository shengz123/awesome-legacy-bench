package com.alphaentropy.domain.basic;


import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import com.alphaentropy.domain.annotation.MySQLVarchar;
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
public class TopLiquidShareHolder {

    @MySQLSymbolKey
    private String securityCode;

    @MySQLDateKey
    private Date endDate;

    private BigDecimal holderRank;

    @MySQLVarchar("128")
    private String holderName;

    private String holderType;

    private String sharesType;

    private BigDecimal holdNum;

    private BigDecimal freeHoldNumRatio;
}
