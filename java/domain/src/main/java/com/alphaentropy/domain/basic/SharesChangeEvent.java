package com.alphaentropy.domain.basic;

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
public class SharesChangeEvent {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date eventDate;

    //变动后总股本
    private BigDecimal totalSharesAfter;
    //变动前总股本
    private BigDecimal totalSharesBefore;

    @MySQLVarchar("256")
    private String reason;
}
