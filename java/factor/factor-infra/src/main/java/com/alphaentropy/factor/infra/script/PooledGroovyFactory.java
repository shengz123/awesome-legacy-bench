package com.alphaentropy.factor.infra.script;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class PooledGroovyFactory extends BasePooledObjectFactory<Script> {
    private String source;

    public PooledGroovyFactory(String source) {
        this.source = source;
    }

    @Override
    public Script create() throws Exception {
        return new GroovyShell().parse(source);
    }

    @Override
    public PooledObject<Script> wrap(Script script) {
        return new DefaultPooledObject<>(script);
    }
}
