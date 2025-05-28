package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BuiltinFunctionCall {
    private String functionName;
    private List<String> parameters;
}
