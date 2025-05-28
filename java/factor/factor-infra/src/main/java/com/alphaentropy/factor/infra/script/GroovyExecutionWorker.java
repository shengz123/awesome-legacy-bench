package com.alphaentropy.factor.infra.script;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.alphaentropy.factor.infra.GroovyUtil.convertGroovyVariable;

@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GroovyExecutionWorker implements Callable<Object> {

    private String script;
    private String symbol;
    private Map<String, Object> instrumentContext;
    private GroovyScriptCache scriptCache;

    @Override
    public Object call() {
        Script shell = scriptCache.get(script);
        Binding binding = getBinding(instrumentContext);
        shell.setBinding(binding);
        try {
            Object res = shell.run();
            instrumentContext.putAll(binding.getVariables());
            return res;
        } catch (MissingPropertyException me) {
            // do nothing as it's normal, add log here during debug
            log.debug(me.getMessage(), me);
        } catch (Exception e) {
            log.warn("Failed to execute {} for {}", script, symbol, e);
        } finally {
            scriptCache.returnScript(script, shell);
        }
        return null;
    }

    private Binding getBinding(Map<String, Object> map) {
        Binding binding = new Binding();
        for (String key : map.keySet()) {
            Object val = convertGroovyVariable(map.get(key));
            if (val != null) {
                binding.setVariable(key, val);
            }
        }
        return binding;
    }

}
