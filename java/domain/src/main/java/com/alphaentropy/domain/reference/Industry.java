package com.alphaentropy.domain.reference;


import com.alphaentropy.domain.annotation.Cached;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import lombok.*;

@Data
@Builder
@Setter
@Getter
@MySQLTable
@NoArgsConstructor
@AllArgsConstructor
@Cached(frequency = "ref")
public class Industry {
    @MySQLSymbolKey
    private String symbol;

    private String catalog;

    private String level1Code;
    private String level1Name;

    private String level2Code;
    private String level2Name;
}
