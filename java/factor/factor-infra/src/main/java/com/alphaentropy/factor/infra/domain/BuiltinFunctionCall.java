package com.alphaentropy.factor.infra.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BuiltinFunctionCall {
    private String functionName;
    private String[] parameters;

}
