package com.alphaentropy.query.domain;

import lombok.Builder;
import lombok.Data;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryParam {
    private String condition;
    private List<String> returnedColumns;
}
