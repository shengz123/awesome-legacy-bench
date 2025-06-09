package com.alphaentropy.domain.reference;

import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import lombok.*;

@Data
@Builder
@Setter
@Getter
//@MySQLTable
@NoArgsConstructor
@AllArgsConstructor
public class SymbolName {
    @MySQLSymbolKey
    private String symbol;

}
