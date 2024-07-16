package com.origeek.imageViewer.util


expect annotation class IntRangeImpl(
    /** Smallest value, inclusive */
    val from: Long = Long.MIN_VALUE,
    /** Largest value, inclusive */
    val to: Long = Long.MAX_VALUE
)

expect annotation class FloatRangeImpl(
    /** Smallest value. Whether it is inclusive or not is determined by [.fromInclusive] */
    val from: Double = Double.NEGATIVE_INFINITY,
    /** Largest value. Whether it is inclusive or not is determined by [.toInclusive] */
    val to: Double = Double.POSITIVE_INFINITY,
    /** Whether the from value is included in the range */
    val fromInclusive: Boolean = true,
    /** Whether the to value is included in the range */
    val toInclusive: Boolean = true
)