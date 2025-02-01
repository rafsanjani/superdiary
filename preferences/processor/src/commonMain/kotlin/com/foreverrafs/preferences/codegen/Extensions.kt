package com.foreverrafs.preferences.codegen

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

fun Resolver.filterDataClassesWithAnnotation(annotation: String): Sequence<KSClassDeclaration> =
    getSymbolsWithAnnotation(annotation)
        .filterIsInstance<KSClassDeclaration>()
        .filter {
            Modifier.DATA in it.modifiers
        }

fun KSPropertyDeclaration.getValueAsString(): String {
    fun illegalStateException(): Unit =
        throw IllegalStateException(
            "Property and annotation type mismatch for ${this.simpleName.asString()}." +
                "Please Ensure that the property type is the same as the @PreferenceKey type used",
        )

    val resolvedProperty = type.resolve().toClassName()

    val firstAnnotatedValue = annotations
        .firstOrNull()
        ?.arguments
        ?.firstOrNull()
        ?.value
        ?: throw IllegalStateException(
            "The property ${simpleName.asString()} hasn't been annotated with @PreferenceKey." +
                "Please ensure all properties have been annotated",
        )

    val value = when (resolvedProperty) {
        Boolean::class.asClassName() -> firstAnnotatedValue as? Boolean ?: illegalStateException()
        Int::class.asClassName() -> firstAnnotatedValue as? Int ?: illegalStateException()
        Float::class.asClassName() -> firstAnnotatedValue as? Float ?: illegalStateException()
        Long::class.asClassName() -> firstAnnotatedValue as? Long ?: illegalStateException()
        Double::class.asClassName() -> firstAnnotatedValue as? Double ?: illegalStateException()
        String::class.asClassName() -> "\"${firstAnnotatedValue as? String ?: illegalStateException()}\""
        else -> null
    }

    return value.toString()
}
