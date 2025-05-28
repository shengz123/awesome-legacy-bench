package com.alphaentropy.factor.core.infrastructure.script;

import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Lazy
@Component
@Slf4j
public class GroovyScriptCache {
    private static final int CAPACITY = 10;
    private GenericObjectPoolConfig poolConfig;
    private Map<String, GenericObjectPool<Script>> scriptCache;

    @PostConstruct
    private void init() {
        scriptCache = new ConcurrentHashMap<>();
        poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(CAPACITY);
        poolConfig.setMaxIdle(CAPACITY);
    }

    public Script get(String script) {
        GenericObjectPool<Script> pool = scriptCache.get(script);
        try {
            return pool.borrowObject();
        } catch (Exception e) {
            log.error("Failed to borrow script from pool", e);
        }
        return null;
    }

    public void put(String name) {
        if (!scriptCache.containsKey(name)) {
            GenericObjectPool scriptObjectPool = new GenericObjectPool<>(new PooledGroovyFactory(name), poolConfig);
            for (int i = 0; i < CAPACITY; i++) {
                try {
                    scriptObjectPool.addObject();
                } catch (Exception e) {
                    log.error("Failed to pre-load script objects into pool", e);
                }
            }
            scriptCache.put(name, scriptObjectPool);
            log.info("Created cache for script {}.", name);
        }
    }

    public void delete(String name) {
        GenericObjectPool scriptObjectPool = scriptCache.remove(name);
        scriptObjectPool.close();
    }

    public void returnScript(String script, Script shell) {
        GenericObjectPool<Script> pool = scriptCache.get(script);
        pool.returnObject(shell);
    }
}
