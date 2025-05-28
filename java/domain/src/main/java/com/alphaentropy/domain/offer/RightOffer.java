package com.alphaentropy.domain.offer;


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
public class RightOffer {
    @MySQLSymbolKey
    private String securityCode;

    //配股上市日
    @MySQLDateKey
    private Date listingDate;
    //配股执行日
    private Date exDividendDate;
    //配股执行日前一交易日
    private Date equityRecordDate;
    //配股比例
    private BigDecimal placingRatio;
    private BigDecimal issuePrice;
    private BigDecimal issueNum;
    private BigDecimal totalSharesBefore;
    private BigDecimal totalSharesAfter;
    private BigDecimal netRaiseFunds;
}
