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
public class FundHolder {

    @MySQLSymbolKey
    private String securityCode;

    @MySQLDateKey
    private Date reportDate;

    private String fundCode;

    @MySQLVarchar("128")
    private String holderName;

    private String holderCode;

    private BigDecimal totalShares;

    private BigDecimal totalsharesRatio;

    private BigDecimal freesharesRatio;

    private BigDecimal freeShares;

}
