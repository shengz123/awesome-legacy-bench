package com.alphaentropy.factor.infra.domain;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SourceFile {
    private String fileName;
    private String defaultClass;
    private Set<String> output;
    private List<CompiledLine> lines;
    private String saveFrequency;
}
