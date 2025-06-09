package com.alphaentropy.factor.infra;

class SingularPairValue implements Serializable {
    def left
    def right

    SingularPairValue(def left, def right) {
        this.left = left
        this.right = right
    }

    def product() {
        if (left != null && right != null) {
            return left.multiply(right)
        }
        return null
    }

    def div() {
        if (left != null && right != null) {
            return left.div(right)
        }
        return null
    }
}
