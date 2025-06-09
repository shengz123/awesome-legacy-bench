package com.alphaentropy.domain.offer;


import com.alphaentropy.domain.annotation.*;
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
public class IssueOffer {
    @MySQLSymbolKey
    private String securityCode;

    private BigDecimal issuePrice;
    private BigDecimal issueNum;
    private BigDecimal issueShareBefore;
    private BigDecimal issueShareAfter;
    private BigDecimal netRaiseFunds;
    @MySQLVarchar("256")
    private String issueObject;
    //增发上市日
    @MySQLDateKey
    private Date issueDate;
    private String lockinPeriod;
}
