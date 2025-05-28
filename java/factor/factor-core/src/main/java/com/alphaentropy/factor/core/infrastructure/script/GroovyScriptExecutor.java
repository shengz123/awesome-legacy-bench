package com.alphaentropy.factor.core.infrastructure.script;

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
public class GroovyScriptExecutor implements ScriptExecutor {

    @Autowired
    private GroovyScriptCache scriptCache;

    private static final int NUM_WORKERS = 10;

    private ExecutorService executorService;

    @PostConstruct
    private void initThreadPool() {
        this.executorService = Executors.newFixedThreadPool(NUM_WORKERS, new ThreadFactoryBuilder().setNameFormat("GroovyExecutorThread-%d").build());
    }

    @Override
    public Set<Map<String, String>> execute(String script, Map<Map<String, String>, Map<String, Object>> instrumentIdsBindings) {
        Set<Map<String, String>> failedIds = ConcurrentHashMap.newKeySet();
        CompletionService completionService = new ExecutorCompletionService(executorService);
        int numProcessedIds = 0;
        scriptCache.put(script);
        for (Map<String, String> instrumentId : instrumentIdsBindings.keySet()) {
            Map<String, Object> instrumentContext = instrumentIdsBindings.get(instrumentId);
            completionService.submit(new GroovyExecutionWorker(script, instrumentId, failedIds, instrumentContext, scriptCache));
            numProcessedIds++;
        }
        waitForResult(completionService, numProcessedIds);
        log.info("Processed {} instruments for {}", numProcessedIds, script);
        return failedIds;
    }

    @Override
    public Map<Map<String, String>, Map<String, String>> executeScriptSet(Map<String, String> scripts, Map<Map<String, String>, Map<String, Object>> instrumentIdsBindings, Map<Map<String, String>, Map<String, String>> insIndexMap) {
        Map<Map<String, String>, Map<String, String>> res = new HashMap<>();
        for (Map<String, String> key : insIndexMap.keySet()) {
            res.put(key, new HashMap<>());
        }
        CompletionService completionService = new ExecutorCompletionService(executorService);
        for (String scriptField : scripts.keySet()) {
            String script = scripts.get(scriptField);
            scriptCache.put(script);
            Set<Map<String, String>> failedIds = ConcurrentHashMap.newKeySet();
            List<Future<Object>> defaultTasks = new ArrayList<>(insIndexMap.size());
            for (Map<String, String> instrument : insIndexMap.keySet()) {
                defaultTasks.add(completionService.submit(new GroovyExecutionWorker(script, instrument, failedIds, instrumentIdsBindings.get(instrument), scriptCache)));
            }
            int index = 0;
            for (Map<String, String> instrument : insIndexMap.keySet()) {
                try {
                    Object r = defaultTasks.get(index).get();
                    res.get(instrument).put(scriptField, Optional.ofNullable(r).map(Object::toString).orElse(null));
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Failed to get result for default value", e);
                }
            }
        }
        log.info("Finished execution for default value.");
        return res;
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
