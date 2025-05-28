package com.alphaentropy.factor.core.infrastructure.script

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation

class FieldPairValue implements Serializable {
    def left
    def right

    FieldPairValue(def left, def right) {
        this.left = left
        this.right = right
    }

    def getAt(idx) {
        return new FieldListValue([left[idx], right[idx]])
    }

    def corr() {
        PearsonsCorrelation pc = new PearsonsCorrelation()
        return new FieldSingularValue(new BigDecimal(pc.correlation(getDataPoints(left), getDataPoints(right))))
    }

    def getDataPoints(FieldListValue fieldListValue) {
        return fieldListValue.list.collect { ((BigDecimal) it.value).doubleValue() } as double[]
    }
}
