package com.foreverrafs.preferences.codegen

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

class PreferencesCodeGenerator {

    /**
     * Generates the Preferences class by delegating operations to individual
     * private functions.
     *
     * @param concreteClass This is the main class that will be created. The
     *    name of this class is obtained as a parameter passed to the
     *    annotation
     * @param settingsClass This is the name of the data class which has been
     *    annotated with [@Preference]
     * @param properties These are the individual members of the data class
     */
    fun generatePreferenceClass(
        concreteClass: ClassName,
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
        interfaceClass: ClassName,
    ): FileSpec {
        val preferenceKeyProperties = createPreferenceKeyProperties(properties)

        val preferenceClassBuilder = createPreferenceClassBuilder(
            preferenceClass = concreteClass,
            interfaceClass = interfaceClass,
            settingsClass = settingsClass,
            preferenceKeyProperties = preferenceKeyProperties,
            properties = properties,
        )

        return FileSpec.builder(
            packageName = concreteClass.packageName,
            fileName = concreteClass.simpleName,
        ).addType(preferenceClassBuilder).build()
    }

    fun generatePreferenceInterface(
        preferenceClass: ClassName,
        settingsClass: ClassName,
    ): FileSpec {
        val settingsSpec = PropertySpec.builder(
            name = "settings",
            type = ClassName("kotlinx.coroutines.flow", "Flow").parameterizedBy(
                settingsClass,
            ),
        ).build()

        val saveFunSpec =
            FunSpec.builder("save").addModifiers(KModifier.SUSPEND, KModifier.ABSTRACT)
                .addParameter(
                    "block",
                    LambdaTypeName.get(
                        returnType = settingsClass,
                        parameters = listOf(
                            ParameterSpec.builder("", settingsClass).build(),
                        ),
                    ),
                ).build()

        val snapshotFunSpec = FunSpec.builder("getSnapshot")
            .addModifiers(KModifier.SUSPEND, KModifier.ABSTRACT)
            .returns(settingsClass)
            .build()

        val clearFunSpec = FunSpec.builder("clear")
            .addModifiers(KModifier.ABSTRACT, KModifier.SUSPEND)
            .build()

        val preferenceInterfaceSpec = TypeSpec.interfaceBuilder(
            preferenceClass,
        ).addProperty(settingsSpec)
            .addFunction(clearFunSpec)
            .addFunction(snapshotFunSpec)
            .addFunction(saveFunSpec).build()

        return FileSpec.builder(
            packageName = preferenceClass.packageName,
            fileName = preferenceClass.simpleName,
        ).addType(preferenceInterfaceSpec).build()
    }

    private fun createPreferenceClassBuilder(
        preferenceClass: ClassName,
        settingsClass: ClassName,
        preferenceKeyProperties: List<PropertySpec>,
        properties: List<KSPropertyDeclaration>,
        interfaceClass: ClassName,
    ): TypeSpec {
        val dataStore = ClassName("androidx.datastore.core", "DataStore")
        val preferences = ClassName("androidx.datastore.preferences.core", "Preferences")

        val constructor = createConstructor()

        val settingsProperty = createSettingsPropertySpec(settingsClass, properties)
        val getSnapshotFun = createGetSnapshotFunctionSpec(settingsClass, properties)
        val saveFun = createSaveFunctionSpec(settingsClass, properties)
        val clearFun = createClearFunctionSpec()
        val companionObject = createCompanionObjectSpec(preferenceClass)

        return TypeSpec.classBuilder(preferenceClass)
            .addSuperinterfaces(
                listOf(interfaceClass),
            )
            .primaryConstructor(constructor).addProperty(
                PropertySpec.builder("dataStore", dataStore.parameterizedBy(preferences))
                    .initializer("dataStore").addModifiers(KModifier.PRIVATE).build(),
            )
            .addProperties(preferenceKeyProperties)
            .addProperty(settingsProperty)
            .addFunction(getSnapshotFun)
            .addFunction(saveFun)
            .addFunction(clearFun)
            .addType(companionObject)
            .build()
    }

    private fun createConstructor(): FunSpec = FunSpec.constructorBuilder()
        .addParameter(
            ParameterSpec.builder(
                "dataStore",
                ClassName("androidx.datastore.core", "DataStore").parameterizedBy(
                    ClassName("androidx.datastore.preferences.core", "Preferences"),
                ),
            ).build(),
        ).build()

    /**
     * Creates a settings property which emits changes to the preferences. The
     * generated property is a pure Kotlin data class and can be observed as is
     */
    private fun createSettingsPropertySpec(
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): PropertySpec {
        val flow = ClassName("kotlinx.coroutines.flow", "Flow")
        val map = MemberName("kotlinx.coroutines.flow", "map")

        return PropertySpec.builder("settings", flow.parameterizedBy(settingsClass)).initializer(
            "dataStore.data.%M { prefs -> %T(%L) }",
            map,
            settingsClass,
            properties.joinToString(",\n") { property ->
                val key = property.simpleName.asString()
                "$key = prefs[${key}Key] ?: ${property.getValueAsString()}"
            },
        )
            .addModifiers(KModifier.OVERRIDE)
            .build()
    }

    /**
     * Generate a snapshot function for users who may not want to observe
     * changes. This grabs the latest value at a specific point in time and may
     * not always reflect the most up to date value
     */
    private fun createGetSnapshotFunctionSpec(
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): FunSpec {
        val first = MemberName("kotlinx.coroutines.flow", "first")

        return FunSpec.builder("getSnapshot")
            .addModifiers(KModifier.SUSPEND, KModifier.OVERRIDE)
            .returns(settingsClass)
            .addCode(
                "val prefs = dataStore.data.%M()\nreturn %T(%L)",
                first,
                settingsClass,
                properties.joinToString(",\n") { property ->
                    val key = property.simpleName.asString()
                    "$key = prefs[${key}Key] ?: ${property.getValueAsString()}"
                },
            ).build()
    }

    private fun createSaveFunctionSpec(
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): FunSpec {
        val edit = MemberName("androidx.datastore.preferences.core", "edit")

        return FunSpec.builder("save")
            .addModifiers(KModifier.SUSPEND, KModifier.OVERRIDE)
            .addParameter(
                "block",
                LambdaTypeName.get(
                    returnType = settingsClass,
                    parameters = listOf(
                        ParameterSpec.builder("", settingsClass).build(),
                    ),
                ),
            ).addCode(
                """
                    val currentSettings = getSnapshot()
                    val settings = block(currentSettings)
                    dataStore.%M {
                        %L
                    }

                """.trimIndent(),
                edit,
                properties.joinToString("\n") { property ->
                    val key = property.simpleName.asString()
                    "it[${key}Key] = settings.$key"
                },
            ).build()
    }

    private fun createClearFunctionSpec(): FunSpec {
        val edit = MemberName("androidx.datastore.preferences.core", "edit")

        return FunSpec.builder("clear")
            .addModifiers(KModifier.SUSPEND, KModifier.OVERRIDE)
            .addCode("dataStore.%M { it.clear() }", edit)
            .build()
    }

    private fun createCompanionObjectSpec(preferenceClass: ClassName): TypeSpec =
        TypeSpec.companionObjectBuilder().addAnnotation(
            AnnotationSpec.builder(ClassName("kotlinx.coroutines", "InternalCoroutinesApi"))
                .build(),
        ).addProperty(
            PropertySpec.builder(
                "instance",
                preferenceClass.copy(nullable = true),
            )
                .mutable()
                .addModifiers(KModifier.PRIVATE).initializer("null")
                .build(),
        ).addFunction(
            FunSpec.builder("getInstance")
                .returns(preferenceClass)
                .addParameter(
                    ParameterSpec.builder(
                        name = "dataStore",
                        // DataStore<Preferences>
                        type = ClassName(
                            "androidx.datastore.core",
                            "DataStore",
                        ).parameterizedBy(
                            ClassName("androidx.datastore.preferences.core", "Preferences"),
                        ),
                    )
                        .build(),
                )
                .addCode(
                    "return instance ?: %T(dataStore).also { instance = it }",
                    preferenceClass,
                )
                .build(),
        )
            .build()

    private fun createPreferenceKeyProperties(properties: List<KSPropertyDeclaration>): List<PropertySpec> =
        properties.map { property ->
            PropertySpec.builder(
                "${property.simpleName.getShortName()}Key",
                ClassName("androidx.datastore.preferences.core.Preferences", "Key").parameterizedBy(
                    property.type.resolve().toClassName(),
                ),
            )
                .addModifiers(KModifier.PRIVATE)
                .initializer(
                    getPreferenceInitializer(property = property),
                )
                .build()
        }

    // Example generated line: val isFirstLaunchKey = booleanPreferencesKey("isFirstLaunch")
    private fun getPreferenceInitializer(property: KSPropertyDeclaration): CodeBlock {
        val keyFunction = when (property.type.resolve().toClassName()) {
            Boolean::class.asClassName() -> "booleanPreferencesKey"
            Int::class.asClassName() -> "intPreferencesKey"
            Float::class.asClassName() -> "floatPreferencesKey"
            Long::class.asClassName() -> "longPreferencesKey"
            Double::class.asClassName() -> "doublePreferencesKey"
            String::class.asClassName() -> "stringPreferencesKey"
            else -> throw IllegalArgumentException("Unsupported type: ${property.type}")
        }

        return CodeBlock.of(
            "%M(%S)",
            MemberName(
                "androidx.datastore.preferences.core",
                keyFunction,
            ),
            property,
        )
    }
}
