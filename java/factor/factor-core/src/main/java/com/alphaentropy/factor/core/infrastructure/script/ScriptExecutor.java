package com.alphaentropy.factor.core.infrastructure.script;

import java.util.Map;
import java.util.Set;

public interface ScriptExecutor {
    Set<Map<String, String>> execute(String script, Map<Map<String, String>, Map<String, Object>> instrumentIdsBindings);

    Map<Map<String, String>, Map<String, String>> executeScriptSet(Map<String, String> scripts, Map<Map<String, String>, Map<String, Object>> instrumentIdsBindings,
                                                                   Map<Map<String, String>, Map<String, String>> insIndexMap);
}
