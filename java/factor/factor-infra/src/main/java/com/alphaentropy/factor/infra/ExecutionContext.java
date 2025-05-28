package com.alphaentropy.factor.infra;

import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExecutionContext {
    private Collection<String> symbols;
    private Date valueDate;
}
