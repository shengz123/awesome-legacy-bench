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
public class HolderSharesChange {
    @MySQLSymbolKey
    private String securityCode;
    @MySQLDateKey
    private Date noticeDate;

    private Date tradeDate;

    private BigDecimal tradeAveragePrice;

    private BigDecimal changeNumSymbol;
    @MySQLVarchar("512")
    private String holderName;

    private String market;
}
