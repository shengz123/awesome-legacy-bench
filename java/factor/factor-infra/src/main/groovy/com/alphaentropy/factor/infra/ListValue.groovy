package com.alphaentropy.factor.infra;

import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString

class ListValue implements Serializable {
    def list

    ListValue() {}

    ListValue(def list) {
        this.list = list
    }

    ListValue loop(@ClosureParams(value = FromString.class, options = "T,java.lang.Integer") Closure transform) {
        List<SingularValue> result = new ArrayList<>()
        for (int i = 0; i < list.size(); i++) {
            result.add(transform.call(i, list.get(i)))
        }
        return new ListValue(result)
    }

    def sum() {
        return list.sum()
    }

    def shift(offset) {
        List<SingularValue> result = new ArrayList<>()
        if (offset < 0) {
            for (int i = (0 - offset); i < list.size(); i++) {
                result.add(list[i]);
            }
        } else if (offset > 0) {
            for (int i = 0; i < list.size() - offset; i++) {
                result.add(list[i])
            }
        }
        return new ListValue(result)
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

    def putAt(idx, SingularValue val) {
        if (val != null) {
            list[idx] = val
        }
        return
    }

    def count() {
        return new SingularValue(new BigDecimal(list.size()))
    }

    def size() {
        return list.size()
    }

    def avg() {
        def count = list.size()
        return list.sum() / count
    }

    def max() {
        def ret = new BigDecimal(-99999)
        list.each {
            if (it.value().compareTo(ret) > 0) {
                ret = it.value()
            }
        }
        return new SingularValue(ret)
    }

    def min() {
        def ret = new BigDecimal(99999)
        list.each {
            if (it.value().compareTo(ret) < 0) {
                ret = it.value()
            }
        }
        return new SingularValue(ret)
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

    def plus(SingularValue v) {
        return new ListValue(list*.plus(v))
    }

    def plus(def v) {
        return new ListValue(list*.plus(v))
    }

    def minus(SingularValue v) {
        return new ListValue(list*.minus(v))
    }

    def minus(def v) {
        return new ListValue(list*.minus(v))
    }

    def subList(ListValue bList) {
        // a list - b list = ret
        def rLen = this.count().min(bList.count())
        List<SingularValue> result = new ArrayList<>()

        for (int i = 0; i < rLen.value(); i++) {
            result.add(this.list[i] - bList.list[i]);
        }
        return new ListValue(result)
    }

    def addList(ListValue bList) {
        // a list + b list = ret
        def rLen = this.count().min(bList.count())
        List<SingularValue> result = new ArrayList<>()

        for (int i = 0; i < rLen.value(); i++) {
            result.add(this.list[i] + bList.list[i]);
        }
        return new ListValue(result)
    }

    def mulList(ListValue bList) {
        // a list + b list = ret
        def rLen = this.count().min(bList.count())
        List<SingularValue> result = new ArrayList<>()

        for (int i = 0; i < rLen.value(); i++) {
            result.add(this.list[i] * bList.list[i]);
        }
        return new ListValue(result)
    }

    def divList(ListValue bList) {
        // a list + b list = ret
        def rLen = this.count().min(bList.count())
        List<SingularValue> result = new ArrayList<>()

        for (int i = 0; i < rLen.value(); i++) {
            result.add(this.list[i] / bList.list[i]);
        }
        return new ListValue(result)
    }

    def multiply(SingularValue v) {
        return new ListValue(list*.multiply(v))
    }

    def multiply(def v) {
        return new ListValue(list*.multiply(v))
    }

    def div(SingularValue v) {
        return new ListValue(list*.div(v))
    }

    def div(def v) {
        return new ListValue(list*.div(v))
    }

    def power(SingularValue v) {
        return new ListValue(list*.power(v))
    }

    def power(def v) {
        return new ListValue(list*.power(v))
    }

    def value() {
        return list
    }
}
