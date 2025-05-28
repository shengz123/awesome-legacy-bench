package com.alphaentropy.factor.infra;

import com.alphaentropy.factor.infra.builtin.BuiltinFactory;
import com.alphaentropy.factor.infra.builtin.BuiltinFunction;
import com.alphaentropy.factor.infra.domain.BuiltinFunctionCall;
import com.alphaentropy.factor.infra.domain.CompiledLine;
import com.alphaentropy.factor.infra.domain.SourceFile;
import com.alphaentropy.factor.infra.script.GroovyScriptExecutor;
import com.alphaentropy.store.cache.DataAccessor;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FormulaExecution {

    private ExecutionContext context;

    private GroovyScriptExecutor executor;

    private DataAccessor accessor;

    private Map<String, Map<String, Object>> allSymbolsBindings = new ConcurrentHashMap<>();

    public FormulaExecution(ExecutionContext context, GroovyScriptExecutor executor, DataAccessor accessor) {
        this.context = context;
        this.executor = executor;
        this.accessor = accessor;
    }

    public void execute(SourceFile sourceFile) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (CompiledLine line : sourceFile.getLines()) {
            BuiltinFunctionCall call = line.getBuiltin();
            if (call != null) {
                BuiltinFunction func = BuiltinFactory.getBiFunc(call.getFunctionName());
                Map<String, Object> funcRet = func.invoke(accessor, context.getSymbols(), context.getValueDate(),
                        sourceFile.getDefaultClass(), Arrays.asList(call.getParameters()), null);
                setFuncResultToBinding(line.getBuiltinVar(), funcRet);
            }
            executor.execute(line.getCompiledScript(), allSymbolsBindings);
        }
        collectResult(sourceFile.getOutput(), sourceFile.getSaveFrequency());
        log.info("Finish executing formula file {} for {}, took {} min", sourceFile.getFileName(),
                context.getValueDate(), stopwatch.elapsed(TimeUnit.MINUTES));
    }

    private void collectResult(Set<String> output, String frequency) {
        for (String symbol : allSymbolsBindings.keySet()) {
            Map<String, Object> symbolBinding = allSymbolsBindings.get(symbol);
            Map<String, Object> ret = new HashMap<>();
            for (String out : output) {
                Object val = symbolBinding.get(out);
                if (val != null) {
                    ret.put(out, getValue(val));
                }
            }
            accessor.put(symbol, context.getValueDate(), ret, frequency);
        }
    }

    private BigDecimal getValue(Object bindingVal) {
        SingularValue val = (SingularValue) bindingVal;
        return val.getValue().setScale(5, RoundingMode.HALF_UP);
    }

    private void setFuncResultToBinding(String variable, Map<String, Object> funcRet) {
        for (String symbol : funcRet.keySet()) {
            Map<String, Object> perSymbolBinding = allSymbolsBindings.computeIfAbsent(symbol, v -> new HashMap<>());
            perSymbolBinding.put(variable, funcRet.get(symbol));
        }
    }
}
