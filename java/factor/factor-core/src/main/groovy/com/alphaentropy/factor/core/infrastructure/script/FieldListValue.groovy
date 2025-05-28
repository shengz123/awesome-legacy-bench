package com.alphaentropy.factor.core.infrastructure.script

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class FieldListValue implements Serializable {
    def list

    FieldListValue() {}

    FieldListValue(def list) {
        this.list = list
    }

    FieldListValue loop(@ClosureParams(value = FromString.class, options = "T,java.lang.Integer") Closure transform) {
        List<FieldSingularValue> result = new ArrayList<>()
        for (int i = 0; i < list.size(); i++) {
            result.add(transform.call(i, list.get(i)))
        }
        return new FieldListValue(result)
    }

    def sum() {
        return list.sum()
    }

    def shift(offset) {
        List<FieldSingularValue> result = new ArrayList<>()
        if (offset < 0) {
            for (int i = (0 - offset); i < list.size(); i++) {
                result.add(list[i]);
            }
        } else if (offset > 0) {
            for (int i = 0; i < list.size() - offset; i++) {
                result.add(list[i])
            }
        }
        return new FieldListValue(result)
    }

    def getAt(idx) {
        // we still want to proceed the calculation when it runs over bound
        if (idx >= list.size()) {
            return list[idx - 1]
        } else if (idx < 0) {
            return list[0]
        }
        return list[idx]
    }

    def putAt(idx, FieldSingularValue val) {
        if (val != null) {
            list[idx] = val
        }
        return
    }

    def count() {
        return new FieldSingularValue(new BigDecimal(list.size()))
    }

    def avg() {
        def count = list.size()
        return list.sum() / count
    }

    def stdev() {
        def count = list.size()
        def mean = avg()
        def sum = 0
        list.each {
            sum = (it - mean)**2 + sum
        }
        return (sum / (count - 1)).sqrt()
    }

    def plus(FieldSingularValue v) {
        return new FieldListValue(list*.plus(v))
    }

    def plus(def v) {
        return new FieldListValue(list*.plus(v))
    }

    def minus(FieldSingularValue v) {
        return new FieldListValue(list*.minus(v))
    }

    def minus(def v) {
        return new FieldListValue(list*.minus(v))
    }

    def multiply(FieldSingularValue v) {
        return new FieldListValue(list*.multiply(v))
    }

    def multiply(def v) {
        return new FieldListValue(list*.multiply(v))
    }

    def div(FieldSingularValue v) {
        return new FieldListValue(list*.div(v))
    }

    def div(def v) {
        return new FieldListValue(list*.div(v))
    }

    def power(FieldSingularValue v) {
        return new FieldListValue(list*.power(v))
    }

    def power(def v) {
        return new FieldListValue(list*.power(v))
    }

    def value() {
        return list
    }
}
