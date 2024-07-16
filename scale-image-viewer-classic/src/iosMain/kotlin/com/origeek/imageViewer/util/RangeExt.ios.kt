package com.origeek.imageViewer.util

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.ANNOTATION_CLASS
)
actual annotation class IntRangeImpl actual constructor(
    /** Smallest value, inclusive */
    actual val from: Long,
    /** Largest value, inclusive */
    actual val to: Long
)

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.ANNOTATION_CLASS
)
actual annotation class FloatRangeImpl actual constructor(
    /** Smallest value. Whether it is inclusive or not is determined by [.fromInclusive] */
    actual val from: Double,
    /** Largest value. Whether it is inclusive or not is determined by [.toInclusive] */
    actual val to: Double,
    /** Whether the from value is included in the range */
    actual val fromInclusive: Boolean,
    /** Whether the to value is included in the range */
    actual val toInclusive: Boolean
)