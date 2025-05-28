package com.alphaentropy.factor.core.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FormulaExpression {
    private String fieldName;
    private String variableName;
    private Type type;
    private Target target;

    public enum Type {
        context,
        DB,
        constant
    }

    public enum Target {
        index,
        instrument
    }
}
