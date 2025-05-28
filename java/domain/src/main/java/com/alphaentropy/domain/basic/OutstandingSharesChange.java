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
@Deprecated
public class OutstandingSharesChange {
    @MySQLSymbolKey
    private String securityCode;
    @MySQLDateKey
    private Date endDate;

    //总股本
    private BigDecimal totalShares;
    //流通A股
    private BigDecimal listedAShares;
    //流通B股
    private BigDecimal bFreeShare;
    //流通H股
    private BigDecimal hFreeShare;
    //限制性A股
    private BigDecimal limitedAShares;
    //限制性B股
    private BigDecimal limitedBShares;
    //限制性H股
    private BigDecimal limitedHShares;

}
