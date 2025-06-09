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
public class IncomeAllocByIndustry {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date reportDate;

    //按行业
    private String industry;
    //收入(万元)
    private BigDecimal income;
    //成本(万元)
    private BigDecimal cost;
    //利润(万元)
    private BigDecimal profit;
    //毛利率
    private BigDecimal grossEarnRatio;
    //利润占比
    private BigDecimal earnAlloc;

}
