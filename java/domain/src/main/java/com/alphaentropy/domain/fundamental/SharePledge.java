package com.alphaentropy.domain.fundamental;


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
public class SharePledge {
    @MySQLSymbolKey
    private String securityCode;

    @MySQLDateKey
    private Date tradeDate;

    private BigDecimal pledgeRatio;
    private BigDecimal repurchaseBalance;
    private BigDecimal pledgeDealNum;
    private BigDecimal repurchaseUnlimitedBalance;
    private BigDecimal repurchaseLimitedBalance;
}
