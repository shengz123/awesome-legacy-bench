package com.alphaentropy.factor.core.application.dependency;

import com.alphaentropy.factor.core.domain.FormulaCall;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResolveResult {
    private List<FormulaCall> calls;
    private Map<FormulaCall, Set<String>> fieldDependencies;
}
