package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.domain.annotation.Builtin;
import com.alphaentropy.store.cache.DataAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Builtin("RANK")
public class Rank extends AbstractBuiltFunction {
    @Override
    public Map<String, Object> invoke(DataAccessor accessor, Collection<String> symbols, Date valueDate,
                                      String defaultScope, List<String> params, Map<String, Object> contextParams) {
        return null;
    }
}
