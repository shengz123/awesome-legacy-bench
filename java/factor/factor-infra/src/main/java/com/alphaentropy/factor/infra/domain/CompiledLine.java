package com.alphaentropy.factor.infra.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CompiledLine {
    private String source;
    private BuiltinFunctionCall builtin;
    private String builtinVar;
    private String compiledScript;
}
