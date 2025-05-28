package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CompiledFormula {
    private Formula formula;
    private Set<String> referenceFields;
    private List<NormalizedScriptLine> normalizedScripts;
}
