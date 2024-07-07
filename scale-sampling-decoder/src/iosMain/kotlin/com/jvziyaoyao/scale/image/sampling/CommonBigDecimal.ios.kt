package com.jvziyaoyao.scale.image.sampling

import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSDecimalNumberHandler
import platform.Foundation.NSRoundingMode

actual class CommonBigDecimal {
    private val decimalNumber: NSDecimalNumber

    actual operator fun times(other: CommonBigDecimal): CommonBigDecimal {
        return CommonBigDecimal(decimalNumber.decimalNumberByMultiplyingBy(other.decimalNumber).doubleValue)
    }

    actual operator fun compareTo(other: CommonBigDecimal): Int {
        return decimalNumber.compare(other.decimalNumber).toInt()
    }

    actual fun toFloat(): Float {
        return decimalNumber.floatValue
    }

    actual fun multiply(bigDecimal: CommonBigDecimal): CommonBigDecimal {
        return CommonBigDecimal(decimalNumber.decimalNumberByMultiplyingBy(bigDecimal.decimalNumber).doubleValue)
    }

    actual fun divide(
        realArea: CommonBigDecimal,
        scale: Int,
        roundingMode: CommonRoundingMode
    ): CommonBigDecimal {
        val handler = NSDecimalNumberHandler.defaultDecimalNumberHandler
        val result = decimalNumber.decimalNumberByDividingBy(realArea.decimalNumber)
            .decimalNumberByRoundingAccordingToBehavior(handler)
        return CommonBigDecimal(result.doubleValue)
    }


    actual constructor(ina: CharArray, offset: Int, len: Int) {
        val numberString = ina.concatToString(offset, offset + len)
        decimalNumber = NSDecimalNumber(string = numberString)
    }

    actual constructor(ina: CharArray, offset: Int, len: Int, mc: CommonMathContext) {
        val numberString = ina.concatToString(offset, offset + len)
        decimalNumber = NSDecimalNumber(string = numberString).roundingMode(mc.roundingMode)
    }

    actual constructor(ina: CharArray) {
        decimalNumber = NSDecimalNumber(string = ina.concatToString())
    }

    actual constructor(ina: CharArray, mc: CommonMathContext) {
        decimalNumber = NSDecimalNumber(string = ina.concatToString()).roundingMode(mc.roundingMode)
    }

    actual constructor(vala: String) {
        decimalNumber = NSDecimalNumber(string = vala)
    }

    actual constructor(vala: String, mc: CommonMathContext) {
        decimalNumber = NSDecimalNumber(string = vala).roundingMode(mc.roundingMode)
    }

    actual constructor(vala: Double) {
        decimalNumber = NSDecimalNumber(double = vala)
    }

    actual constructor(vala: Double, mc: CommonMathContext) {
        decimalNumber = NSDecimalNumber(double = vala).roundingMode(mc.roundingMode)
    }

    actual constructor(vala: Int) {
        decimalNumber = NSDecimalNumber(integer = vala.toLong())
    }

    actual constructor(vala: Int, mc: CommonMathContext) {
        decimalNumber = NSDecimalNumber(integer = vala.toLong()).roundingMode(mc.roundingMode)
    }

    actual constructor(vala: Long) {
        decimalNumber = NSDecimalNumber(longLong = vala)
    }

    actual constructor(vala: Long, mc: CommonMathContext) {
        decimalNumber = NSDecimalNumber(longLong = vala).roundingMode(mc.roundingMode)
    }

    private fun NSDecimalNumber.roundingMode(roundingMode: CommonRoundingMode): NSDecimalNumber {
        val handler = when (roundingMode) {
            CommonRoundingMode.HALF_EVEN -> NSDecimalNumberHandler.defaultDecimalNumberHandler
            CommonRoundingMode.UP -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundUp,
                scale = 0.toShort(),
                raiseOnExactness = false,
                raiseOnOverflow = false,
                raiseOnUnderflow = false,
                raiseOnDivideByZero = false
            )

            CommonRoundingMode.DOWN -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundDown,
                scale = 0.toShort(),
                raiseOnExactness = false,
                raiseOnOverflow = false,
                raiseOnUnderflow = false,
                raiseOnDivideByZero = false
            )

            CommonRoundingMode.CEILING -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundUp,  // 向上取整
                scale = 0.toShort(),
                raiseOnExactness = false,
                raiseOnOverflow = false,
                raiseOnUnderflow = false,
                raiseOnDivideByZero = false
            )

            CommonRoundingMode.FLOOR -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundDown,  // 向下取整
                scale = 0.toShort(),
                raiseOnExactness = false,
                raiseOnOverflow = false,
                raiseOnUnderflow = false,
                raiseOnDivideByZero = false
            )

            CommonRoundingMode.HALF_UP -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundPlain,
                scale = 0.toShort(),
                raiseOnExactness = false,
                raiseOnOverflow = false,
                raiseOnUnderflow = false,
                raiseOnDivideByZero = false
            )

            CommonRoundingMode.HALF_DOWN -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundPlain,
                scale = 0.toShort(),
                raiseOnExactness = false,
                raiseOnOverflow = false,
                raiseOnUnderflow = false,
                raiseOnDivideByZero = false
            )

            CommonRoundingMode.UNNECESSARY -> NSDecimalNumberHandler(
                roundingMode = NSRoundingMode.NSRoundPlain,
                scale = 0.toShort(),
                raiseOnExactness = true,
                raiseOnOverflow = true,
                raiseOnUnderflow = true,
                raiseOnDivideByZero = true
            )
        }
        return this.decimalNumberByRoundingAccordingToBehavior(handler)
    }

}