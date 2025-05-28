package com.alphaentropy.factor.core.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FormulaOutput {
    private String fieldName;
    private String variableName;
    private String defaultValue;
    private String initialValue;
}
