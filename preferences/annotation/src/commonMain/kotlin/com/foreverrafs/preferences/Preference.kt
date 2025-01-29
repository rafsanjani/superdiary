package com.foreverrafs.preferences

@Target(AnnotationTarget.CLASS)
annotation class Preference(
    val name: String,
)

@Target(AnnotationTarget.PROPERTY)
annotation class PreferenceKey(val defaultValue: String)
