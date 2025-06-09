package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class NormalizedScriptLine {
    private String source;
    private Map<String, BuiltinFunctionCall> builtin;
}
