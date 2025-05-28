package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Formula {
    private String name;
    private List<String> parameters;
    private List<String> rawLines;
}
