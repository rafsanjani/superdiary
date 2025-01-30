@file:Suppress("UnnecessaryVariable")

package com.foreverrafs.preferences

import com.foreverrafs.preferences.codegen.PreferencesCodeGenerator
import com.foreverrafs.preferences.codegen.filterDataClassesWithAnnotation
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class PreferenceProcessor(
    private val options: Map<String, String>,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) : SymbolProcessor {
    private val preferencesCodeGenerator = PreferencesCodeGenerator()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver
            .filterDataClassesWithAnnotation(PREFERENCE_ANNOTATION_FQN)

        if (!symbols.iterator().hasNext()) return emptyList()

        symbols.forEach { symbol ->
            symbol.accept(
                visitor = Visitor(
                    codeGenerator = codeGenerator,
                    resolver = resolver,
                ),
                data = Unit,
            )
        }

        val unableToProcess = symbols.filterNot { it.validate() }.toList()
        return unableToProcess
    }

    inner class Visitor(
        private val codeGenerator: CodeGenerator,
        private val resolver: Resolver,
    ) : KSVisitorVoid() {

        override fun visitClassDeclaration(
            classDeclaration: KSClassDeclaration,
            data: Unit,
        ) {
            // Getting the list of member properties of the annotated interface.
            val properties = classDeclaration
                .getAllProperties()
                .filter { it.validate() }

            val annotation: KSAnnotation = classDeclaration.annotations.first {
                it.shortName.asString() == PREFERENCE_ANNOTATION_SIMPLE_NAME
            }

            val nameArgument = annotation.arguments
                .first { arg -> arg.name?.asString() == "generatedClassName" }

            preferencesCodeGenerator.generatePreferenceInterface(
                preferenceClass = ClassName(
                    packageName = ROOT_PACKAGE,
                    nameArgument.value as String,
                ),
                settingsClass = classDeclaration.toClassName(),
            ).writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies(
                    aggregating = false,
                    sources = resolver.getAllFiles().toList().toTypedArray(),
                ),
            )

            preferencesCodeGenerator.generatePreferenceClass(
                concreteClass = ClassName(
                    packageName = ROOT_PACKAGE,
                    "${nameArgument.value as String}Impl",
                ),
                interfaceClass = ClassName(
                    packageName = ROOT_PACKAGE,
                    nameArgument.value as String,
                ),
                settingsClass = classDeclaration.toClassName(),
                properties = properties.toList(),
            ).writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies(
                    aggregating = false,
                    sources = resolver.getAllFiles().toList().toTypedArray(),
                ),
            )
        }
    }

    companion object {
        private const val PREFERENCE_ANNOTATION_SIMPLE_NAME = "Preference"
        private const val ROOT_PACKAGE = "com.foreverrafs.preferences"
        private const val PREFERENCE_ANNOTATION_FQN = "$ROOT_PACKAGE.Preference"
    }
}

class PreferenceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        PreferenceProcessor(
            options = environment.options,
            logger = environment.logger,
            codeGenerator = environment.codeGenerator,
        )
}
