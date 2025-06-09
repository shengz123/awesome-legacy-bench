package com.alphaentropy.domain.common;

import lombok.*;

import java.util.Date;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SymbolDateKey {
    private String symbol;
    private Date date;
}
