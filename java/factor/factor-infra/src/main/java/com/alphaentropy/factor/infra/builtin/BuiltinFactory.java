package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.common.utils.BeanUtil;
import com.alphaentropy.domain.annotation.Builtin;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuiltinFactory {
    private static final String BASE_PKG = "com.alphaentropy.factor.infra.builtin";

    private static final Map<String, BuiltinFunction> funcMap = new HashMap<>();

    static {
        List<Class> classList = BeanUtil.findClassesWithAnnotation(BASE_PKG, Builtin.class);
        for (Class clz : classList) {
            Builtin builtin = (Builtin) clz.getAnnotation(Builtin.class);
            funcMap.put(builtin.value(), (BuiltinFunction) BeanUtils.instantiateClass(clz));
        }
    }

    public static BuiltinFunction getBiFunc(String funcName) {
        return funcMap.get(funcName);
    }
}
