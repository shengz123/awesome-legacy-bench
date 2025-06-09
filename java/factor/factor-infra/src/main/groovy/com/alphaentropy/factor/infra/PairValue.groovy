package com.alphaentropy.factor.infra;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation

class PairValue implements Serializable {
    def left
    def right

    PairValue(def left, def right) {
        this.left = left
        this.right = right
    }

    def getAt(idx) {
        return new ListValue([left[idx], right[idx]])
    }

    def product() {
        List<SingularValue> result = new ArrayList<>()
        for (int i = 0; i < left.size(); i++) {
            if (left[i] != null && right[i] != null) {
                result.add(left[i].multiply(right[i]))
            }
        }
        return result
    }

    def div() {
        List<SingularValue> result = new ArrayList<>()
        for (int i = 0; i < left.size(); i++) {
            if (left[i] != null && right[i] != null) {
                result.add(left[i].div(right[i]))
            }
        }
        return result
    }

    def corr() {
        PearsonsCorrelation pc = new PearsonsCorrelation()
        return new SingularValue(new BigDecimal(pc.correlation(getDataPoints(left), getDataPoints(right))))
    }

    def getDataPoints(ListValue fieldListValue) {
        return fieldListValue.list.collect { ((BigDecimal) it.value).doubleValue() } as double[]
    }
}
