package com.alphaentropy.factor.core.infrastructure.script

class FieldSingularValue implements Serializable {
    BigDecimal value

    FieldSingularValue(def value) {
        this.value = value
    }

    def plus(FieldSingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new FieldSingularValue(value + v.value)
    }

    def plus(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(value + v)
    }

    def minus(FieldSingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new FieldSingularValue(value - v.value)
    }

    def minus(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(value - v)
    }

    def multiply(FieldSingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new FieldSingularValue(value * v.value)
    }

    def multiply(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(value * v)
    }

    def div(FieldSingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new FieldSingularValue(value / v.value)
    }

    def div(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(value / v)
    }

    def negative() {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(0 - value)
    }

    def ln() {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(new BigDecimal(Math.log(value)))
    }

    def power(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(value.power(v))
    }

    def power(FieldSingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new FieldSingularValue(new BigDecimal(value.power(v.value)))
    }

    def exp(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(new BigDecimal(Math.pow(v, value)))
    }

    def sqrt(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(new BigDecimal(Math.sqrt((BigDecimal) value)))
    }

    def reciprocal(def v) {
        if (value == null) {
            return this
        }
        return new FieldSingularValue(1 / value)
    }

    def value() {
        return value
    }

    @Override
    String toString() {
        return value.toPlainString()
    }
}
