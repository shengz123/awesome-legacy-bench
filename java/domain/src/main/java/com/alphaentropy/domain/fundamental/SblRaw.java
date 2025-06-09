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
public class SblRaw {
    @MySQLSymbolKey
    private String scode;
    @MySQLDateKey
    private Date date;

    //融资余额
    private BigDecimal rzye;
    //融券余量
    private BigDecimal rqyl;
    //融券余额
    private BigDecimal rqye;
}
