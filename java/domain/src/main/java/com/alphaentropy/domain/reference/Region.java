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
public class Region {
    @MySQLSymbolKey
    private String symbol;

    private String region;
}
