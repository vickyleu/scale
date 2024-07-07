package com.jvziyaoyao.scale.image.sampling

import java.math.RoundingMode

actual class CommonBigDecimal {
    private val bigDecimal: java.math.BigDecimal

    actual operator fun times(other: CommonBigDecimal): CommonBigDecimal {
        return CommonBigDecimal(bigDecimal.multiply(other.bigDecimal).toDouble())
    }

    actual operator fun compareTo(other: CommonBigDecimal): Int {
        return bigDecimal.compareTo(other.bigDecimal)
    }

    actual fun toFloat(): Float {
        return bigDecimal.toFloat()
    }

    actual fun multiply(bigDecimal: CommonBigDecimal): CommonBigDecimal {
        return CommonBigDecimal(this.bigDecimal.multiply(bigDecimal.bigDecimal).toDouble())
    }

    actual fun divide(
        realArea: CommonBigDecimal,
        scale: Int,
        roundingMode: CommonRoundingMode
    ): CommonBigDecimal {
        val javaRoundingMode = when (roundingMode) {
            CommonRoundingMode.HALF_EVEN -> RoundingMode.HALF_EVEN
            CommonRoundingMode.HALF_UP -> RoundingMode.HALF_UP
            CommonRoundingMode.HALF_DOWN -> RoundingMode.HALF_DOWN
            CommonRoundingMode.UP -> RoundingMode.UP
            CommonRoundingMode.DOWN -> RoundingMode.DOWN
            CommonRoundingMode.CEILING -> RoundingMode.CEILING
            CommonRoundingMode.FLOOR -> RoundingMode.FLOOR
            CommonRoundingMode.UNNECESSARY -> RoundingMode.UNNECESSARY
        }
        return CommonBigDecimal(
            this.bigDecimal.divide(realArea.bigDecimal, scale, javaRoundingMode).toDouble()
        )
    }


    actual constructor(ina: CharArray, offset: Int, len: Int) {
        bigDecimal = java.math.BigDecimal(ina, offset, len)
    }

    actual constructor(
        ina: CharArray,
        offset: Int,
        len: Int,
        mc: CommonMathContext
    ) {
        bigDecimal = java.math.BigDecimal(
            ina, offset, len,
            java.math.MathContext(mc.precision, RoundingMode.valueOf(mc.roundingMode.oldMode))
        )
    }

    actual constructor(ina: CharArray) {
        bigDecimal = java.math.BigDecimal(ina)
    }

    actual constructor(
        ina: CharArray,
        mc: CommonMathContext
    ) {
        bigDecimal = java.math.BigDecimal(
            ina,
            java.math.MathContext(mc.precision, RoundingMode.valueOf(mc.roundingMode.oldMode))
        )
    }

    actual constructor(vala: String) {
        bigDecimal = java.math.BigDecimal(vala)
    }

    actual constructor(
        vala: String,
        mc: CommonMathContext
    ) {
        bigDecimal = java.math.BigDecimal(
            vala,
            java.math.MathContext(mc.precision, RoundingMode.valueOf(mc.roundingMode.oldMode))
        )
    }

    actual constructor(vala: Double) {
        bigDecimal = java.math.BigDecimal(vala)
    }

    actual constructor(
        vala: Double,
        mc: CommonMathContext
    ) {
        bigDecimal = java.math.BigDecimal(
            vala,
            java.math.MathContext(mc.precision, RoundingMode.valueOf(mc.roundingMode.oldMode))
        )
    }

    actual constructor(vala: Int) {
        bigDecimal = java.math.BigDecimal(vala)
    }

    actual constructor(
        vala: Int,
        mc: CommonMathContext
    ) {
        bigDecimal = java.math.BigDecimal(
            vala,
            java.math.MathContext(mc.precision, RoundingMode.valueOf(mc.roundingMode.oldMode))
        )
    }

    actual constructor(vala: Long) {
        bigDecimal = java.math.BigDecimal(vala)
    }

    actual constructor(
        vala: Long,
        mc: CommonMathContext
    ) {
        bigDecimal = java.math.BigDecimal(
            vala,
            java.math.MathContext(mc.precision, RoundingMode.valueOf(mc.roundingMode.oldMode))
        )
    }

}