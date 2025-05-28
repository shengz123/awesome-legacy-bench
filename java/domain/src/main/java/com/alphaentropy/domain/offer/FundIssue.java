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
public class FundIssue {
    @MySQLSymbolKey
    private String fundCode;
    @MySQLVarchar("64")
    private String fundName;
    private String fundCompany;
    private String fundCompanyCode;
    private String fundType;
    private BigDecimal totalUnits;
    @MySQLDateKey
    private Date issuedDate;
    private BigDecimal cumulativePnl;
    private String investManagers;
    private String fundStatus;
    private String purchasePeriod;
}
