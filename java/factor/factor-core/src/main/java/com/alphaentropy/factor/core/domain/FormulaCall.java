package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FormulaCall {
    private String formulaName;
    private Map<String, String> parameters;
    private List<FormulaOutput> outputMapping;
    private int scale;
}
