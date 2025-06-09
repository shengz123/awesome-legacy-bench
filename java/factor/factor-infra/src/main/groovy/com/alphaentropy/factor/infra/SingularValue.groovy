package com.alphaentropy.factor.infra;

class SingularValue implements Serializable {
    BigDecimal value

    SingularValue(def value) {
        this.value = value
    }

    def plus(SingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new SingularValue(value + v.value)
    }

    def plus(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(value + v)
    }

    def minus(SingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new SingularValue(value - v.value)
    }

    def minus(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(value - v)
    }

    def multiply(SingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new SingularValue(value * v.value)
    }

    def multiply(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(value * v)
    }

    def div(SingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new SingularValue(value / v.value)
    }

    def div(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(value / v)
    }

    def negative() {
        if (value == null) {
            return this
        }
        return new SingularValue(0 - value)
    }

    def min(def v) {
        if (value.compareTo(v) < 0) {
            return new SingularValue(value)
        } else {
            return new SingularValue(v)
        }
    }

    def max(def v) {
        if (value.compareTo(v) > 0) {
            return new SingularValue(value)
        } else {
            return new SingularValue(v)
        }
    }

    def ln() {
        if (value == null) {
            return this
        }
        return new SingularValue(new BigDecimal(Math.log(value)))
    }

    def power(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(value.power(v))
    }

    def power(SingularValue v) {
        if (value == null) {
            return this
        }
        if (v.value == null) {
            return v
        }
        return new SingularValue(new BigDecimal(value.power(v.value)))
    }

    def exp(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(new BigDecimal(Math.pow(v, value)))
    }

    def sqrt(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(new BigDecimal(Math.sqrt((BigDecimal) value)))
    }

    def reciprocal(def v) {
        if (value == null) {
            return this
        }
        return new SingularValue(1 / value)
    }

    def value() {
        return value
    }

    @Override
    String toString() {
        return value.toPlainString()
    }
}
