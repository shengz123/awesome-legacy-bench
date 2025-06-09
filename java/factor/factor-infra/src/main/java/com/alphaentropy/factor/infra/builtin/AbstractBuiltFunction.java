package com.alphaentropy.factor.infra.builtin;

import com.alphaentropy.common.utils.BeanUtil;
import com.alphaentropy.domain.annotation.Factor;
import com.alphaentropy.domain.annotation.MySQLTable;
import org.apache.hadoop.fs.DU;

public abstract class AbstractBuiltFunction implements BuiltinFunction {

    private static final String REF = "$";
    private static final String DOT = ".";
    private static final String SEP_DOT = "\\.";
    private static final String DU_REF = "$$";

    protected Class findClassFromVariable(String defaultClz, String param) {
        if (defaultClz != null && !param.contains(DOT)) {
            return BeanUtil.findClassWithAnnotation(MySQLTable.class, defaultClz);
        }
        if (param.contains(DOT)) {
            String clzName = param.split(SEP_DOT)[0].replace(REF, "");
            return BeanUtil.findClassWithAnnotation(MySQLTable.class, clzName);
        }
        if (param.contains(DU_REF) && !param.contains(DOT)) {
            return Factor.class;
        }
        return null;
    }

    protected String findAttrFromVariable(String defaultClz, String param) {
        if (defaultClz != null && !param.contains(DOT)) {
            return param.replace(REF, "");
        } else if (param.contains(DU_REF) && !param.contains(DOT)) {
            return param.replace(REF, "");
        } else if (param.contains(DOT)) {
            return param.split(SEP_DOT)[1].trim();
        }
        return null;
    }
}
