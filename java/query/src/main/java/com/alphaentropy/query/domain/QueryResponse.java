package com.alphaentropy.query.domain;

import lombok.Builder;
import lombok.Data;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {
    private int responseCode;
    private String message;
    private Object result;
}
