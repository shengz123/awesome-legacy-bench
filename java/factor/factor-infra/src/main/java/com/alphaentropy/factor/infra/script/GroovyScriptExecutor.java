package com.alphaentropy.factor.infra.script;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@Lazy
@Component
@Slf4j
public class GroovyScriptExecutor {

    @Autowired
    private GroovyScriptCache scriptCache;

    private static final int NUM_WORKERS = 10;

    private ExecutorService executorService;

    @PostConstruct
    private void initThreadPool() {
        this.executorService = Executors.newFixedThreadPool(NUM_WORKERS,
                new ThreadFactoryBuilder().setNameFormat("GroovyExecutorThread-%d").build());
    }

    public void execute(String script, Map<String, Map<String, Object>> allSymbolsBindings) {
        CompletionService completionService = new ExecutorCompletionService(executorService);
        int numProcessedIds = 0;
        scriptCache.put(script);
        for (String symbol : allSymbolsBindings.keySet()) {
            Map<String, Object> symbolCtx = allSymbolsBindings.get(symbol);
            completionService.submit(new GroovyExecutionWorker(script, symbol, symbolCtx, scriptCache));
            numProcessedIds++;
        }
        waitForResult(completionService, numProcessedIds);
    }

    private void waitForResult(CompletionService completionService, int numProcessedIds) {
        try {
            for (int i = 0; i < numProcessedIds; i++) {
                Future result = completionService.take();
                result.get();
            }
        } catch (Exception e) {
            log.error("Failed to get the groovy execution result", e);
        }
    }
}
