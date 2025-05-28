package com.alphaentropy.domain.fundamental;

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
public class BuyBack {
    @MySQLSymbolKey
    private String dimScode;
    @MySQLDateKey
    private Date updatedate;

    private Date dimDate;

    private Date repurenddate;

    private Date repurstartdate;

    private BigDecimal repuramountlimit;

    private BigDecimal repuramountlower;

    private BigDecimal repurpricecap;

    private BigDecimal repuramount;

    private BigDecimal repurnum;

    @MySQLVarchar("2048")
    private String repurobjective;
}
