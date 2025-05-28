package com.alphaentropy.domain.release;


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
public class ReleaseDigest {
    @MySQLSymbolKey
    private String symbol;
    private String name;

    private BigDecimal earnPerShare;
    private BigDecimal revenue;
    private BigDecimal revenueLast;
    private BigDecimal revenueYoy;
    private BigDecimal revenueQoq;

    private BigDecimal profit;
    private BigDecimal profitLast;
    private BigDecimal profitYoy;
    private BigDecimal profitQoq;

    private BigDecimal netAssetPerShare;
    private BigDecimal roe;

    private Date eventDate;

    @MySQLDateKey
    private Date reportDate;

}
