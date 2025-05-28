package com.alphaentropy.domain.basic;

import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import lombok.*;

import java.util.Date;

@Data
@Builder
@Setter
@Getter
@MySQLTable
@NoArgsConstructor
@AllArgsConstructor
public class IpoDate {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date ipoDate;
}
