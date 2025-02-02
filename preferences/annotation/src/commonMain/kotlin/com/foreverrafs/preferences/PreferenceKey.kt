package com.foreverrafs.preferences

@Suppress("unused")
@Target(AnnotationTarget.PROPERTY)
annotation class PreferenceKey {
    @Target(AnnotationTarget.PROPERTY)
    annotation class String(val default: kotlin.String)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Int(val default: kotlin.Int)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Boolean(val default: kotlin.Boolean)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Float(val default: kotlin.Float)

    @Target(AnnotationTarget.PROPERTY)
    annotation class Double(val default: kotlin.Double)
}
