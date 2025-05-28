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
public class TsBoardSbl {
    @MySQLSymbolKey
    private String scode;

    @MySQLDateKey
    private Date tdate;

    //可出借容量上限
    private BigDecimal limitbalance;

    //出借余量占非限售股的比例(越高越可能被轧空)
    private BigDecimal surplusrate;

    //还可以出借的数量(做空的子弹)
    private BigDecimal lendshares;

    //可出借量占非限售股的比例(越高越可能被做空)
    private BigDecimal stockrate;

    //已出借量
    private BigDecimal lendbalance;

    //可流通股
    private BigDecimal unlimitshares;

}
