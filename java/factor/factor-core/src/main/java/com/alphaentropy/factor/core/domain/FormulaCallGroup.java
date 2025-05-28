package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FormulaCallGroup {
    private String groupName;
    private List<FormulaCall> calls;
}
