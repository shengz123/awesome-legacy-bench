package com.alphaentropy.factor.core.domain;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CalculationContext {
    private Map<Map<String, String>, Map<String, String>> instrumentIndexIdMappings;
    private String date;
    private String formulaCallGroup;

    public void removeInstrument(Map<String, String> instrumentId) {
        this.instrumentIndexIdMappings.remove(instrumentId);
    }

    public CalculationContext deepCopy() {
        Map<Map<String, String>, Map<String, String>> newIns = new HashMap<>(instrumentIndexIdMappings);
        return CalculationContext.builder()
                .instrumentIndexIdMappings(newIns)
                .date(date)
                .formulaCallGroup(formulaCallGroup)
                .build();
    }
}
