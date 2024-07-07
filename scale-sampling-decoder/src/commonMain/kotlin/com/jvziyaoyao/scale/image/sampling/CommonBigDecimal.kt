package com.jvziyaoyao.scale.image.sampling

internal typealias BigDecimal = CommonBigDecimal

expect class CommonBigDecimal {
    constructor(ina: CharArray, offset: Int, len: Int)
    constructor(ina: CharArray, offset: Int, len: Int, mc: CommonMathContext)
    constructor(ina: CharArray)
    constructor(ina: CharArray, mc: CommonMathContext)
    constructor(vala: String)
    constructor(vala: String, mc: CommonMathContext)
    constructor(vala: Double)
    constructor(vala: Double, mc: CommonMathContext)
    constructor(vala: Int)
    constructor(vala: Int, mc: CommonMathContext)
    constructor(vala: Long)
    constructor(vala: Long, mc: CommonMathContext)

    // Methods
    operator fun times(other: CommonBigDecimal): CommonBigDecimal
    operator fun compareTo(other: CommonBigDecimal): Int
    fun multiply(bigDecimal: BigDecimal): CommonBigDecimal
    fun toFloat(): Float
    fun divide(realArea: CommonBigDecimal, scale: Int, roundingMode: CommonRoundingMode): CommonBigDecimal
}

object CommonBigDecimalConstants {
    const val ROUND_UP = 0
    const val ROUND_DOWN = 1
    const val ROUND_CEILING = 2
    const val ROUND_FLOOR = 3
    const val ROUND_HALF_UP = 4
    const val ROUND_HALF_DOWN = 5
    const val ROUND_HALF_EVEN = 6
    const val ROUND_UNNECESSARY = 7
}

enum class CommonRoundingMode(val oldMode: Int) {
    UP(oldMode = CommonBigDecimalConstants.ROUND_UP),
    DOWN(oldMode = CommonBigDecimalConstants.ROUND_DOWN),
    CEILING(oldMode = CommonBigDecimalConstants.ROUND_CEILING),
    FLOOR(oldMode = CommonBigDecimalConstants.ROUND_FLOOR),
    HALF_UP(oldMode = CommonBigDecimalConstants.ROUND_HALF_UP),
    HALF_DOWN(oldMode = CommonBigDecimalConstants.ROUND_HALF_DOWN),
    HALF_EVEN(oldMode = CommonBigDecimalConstants.ROUND_HALF_EVEN),
    UNNECESSARY(oldMode = CommonBigDecimalConstants.ROUND_UNNECESSARY);

    companion object {
        fun valueOf(rm: Int): CommonRoundingMode {
            return when (rm) {
                CommonBigDecimalConstants.ROUND_UP -> UP
                CommonBigDecimalConstants.ROUND_DOWN -> DOWN
                CommonBigDecimalConstants.ROUND_CEILING -> CEILING
                CommonBigDecimalConstants.ROUND_FLOOR -> FLOOR
                CommonBigDecimalConstants.ROUND_HALF_UP -> HALF_UP
                CommonBigDecimalConstants.ROUND_HALF_DOWN -> HALF_DOWN
                CommonBigDecimalConstants.ROUND_HALF_EVEN -> HALF_EVEN
                CommonBigDecimalConstants.ROUND_UNNECESSARY -> UNNECESSARY
                else -> throw IllegalArgumentException("argument out of range")
            }
        }
    }
}

class CommonMathContext {
     var precision: Int = 0
     var roundingMode = CommonRoundingMode.UP

    constructor(precision: Int, roundingMode: CommonRoundingMode) {
        if (precision < MIN_DIGITS)
            throw IllegalArgumentException("Digits < 0")
        this.precision = precision
        this.roundingMode = roundingMode
    }

    constructor(precision: Int) : this(precision, DEFAULT_ROUNDINGMODE)
    constructor(vala: String?) {
        var bad = false
        val setPrecision: Int
        if (vala == null)
            throw NullPointerException("null String")
        try {
            if (!vala.startsWith("precision=")) throw RuntimeException()
            val fence = vala.indexOf(' ')
            var off = 10
            setPrecision = vala.substring(10, fence).toInt()
            if (!vala.startsWith("roundingMode=", fence + 1))
                throw RuntimeException()
            off = fence + 1 + 13
            val str = vala.substring(off, vala.length)
            roundingMode = CommonRoundingMode.valueOf(str)
        } catch (re: RuntimeException) {
            throw IllegalArgumentException("bad string format")
        }
        if (setPrecision < MIN_DIGITS)
            throw IllegalArgumentException("Digits < 0")
        precision = setPrecision
    }


    override fun equals(other: Any?): Boolean {
        if (other !is CommonMathContext)
            return false
        return other.precision == this.precision
                && other.roundingMode == this.roundingMode
    }

    override fun hashCode(): Int {
        return this.precision + roundingMode.hashCode() * 59
    }

    override fun toString(): String {
        return "precision=$precision roundingMode=${roundingMode.toString()}"
    }

    companion object {
        private const val MIN_DIGITS: Int = 0
        private const val DEFAULT_DIGITS: Int = 9
        private val DEFAULT_ROUNDINGMODE: CommonRoundingMode = CommonRoundingMode.HALF_UP

        val UNLIMITED = CommonMathContext(0, CommonRoundingMode.HALF_UP)
        val DECIMAL32 = CommonMathContext(7, CommonRoundingMode.HALF_EVEN)
        val DECIMAL64 = CommonMathContext(16, CommonRoundingMode.HALF_EVEN)
        val DECIMAL128 = CommonMathContext(34, CommonRoundingMode.HALF_EVEN)
    }
}