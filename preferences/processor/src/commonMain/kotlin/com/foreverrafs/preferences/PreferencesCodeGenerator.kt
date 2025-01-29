package com.foreverrafs.preferences

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
     * Generates the Preferences class by delegating activities to individual
     * private functions.
     *
     * @param preferenceClass This is the main class that will be created. The
     *    name of this class is obtained as a parameter passed to the
     *    annotation
     * @param settingsClass This is the name of the data class which has been
     *    annotated with [@Preference]
     * @param properties These are the individual members of the data class
     */
    fun generatePreferenceClass(
        preferenceClass: ClassName,
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): FileSpec {
        val preferenceKeyProperties = createPreferenceKeyProperties(properties)

        val preferenceClassBuilder = createPreferenceClassBuilder(
            preferenceClass = preferenceClass,
            settingsClass = settingsClass,
            preferenceKeyProperties = preferenceKeyProperties,
            properties = properties,
        )

        return FileSpec.builder(preferenceClass.packageName, preferenceClass.simpleName)
            .addType(preferenceClassBuilder)
            .build()
    }

    private fun createPreferenceClassBuilder(
        preferenceClass: ClassName,
        settingsClass: ClassName,
        preferenceKeyProperties: List<PropertySpec>,
        properties: List<KSPropertyDeclaration>,
    ): TypeSpec {
        val dataStore = ClassName("androidx.datastore.core", "DataStore")
        val preferences = ClassName("androidx.datastore.preferences.core", "Preferences")

        val constructor =
            createConstructor()

        val settingsProperty = createSettingsProperty(settingsClass, properties)
        val getSnapshotFun = createGetSnapshotFunction(settingsClass, properties)
        val saveFun = createSaveFunction(settingsClass, properties)
        val clearFun = createClearFunction()
        val companionObject = createCompanionObject(preferenceClass)

        return TypeSpec.classBuilder(preferenceClass)
            .primaryConstructor(constructor)
            .addProperty(
                PropertySpec.builder("dataStore", dataStore.parameterizedBy(preferences))
                    .initializer("dataStore")
                    .addModifiers(KModifier.PRIVATE)
                    .build(),
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
            )
                .build(),
        )
        .build()

    /**
     * Creates a settings property which emits changes to the preferences. The
     * generated property is a pure Kotlin data class and can be observed as is
     */
    private fun createSettingsProperty(
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): PropertySpec {
        val flow = ClassName("kotlinx.coroutines.flow", "Flow")
        val map = MemberName("kotlinx.coroutines.flow", "map")

        return PropertySpec.builder("settings", flow.parameterizedBy(settingsClass))
            .initializer(
                "dataStore.data.%M { prefs -> %T(%L) }",
                map,
                settingsClass,
                properties.joinToString(",\n") { property ->
                    val key = property.simpleName.asString()
                    "$key = prefs[${key}Key] ?: ${getDefaultValue(property)}"
                },
            )
            .build()
    }

    /**
     * Generate a snapshot function for users who may not want to observe
     * changes. This grabs the latest value at a specific point in time and may
     * not always reflect the most up to date value
     */
    private fun createGetSnapshotFunction(
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): FunSpec {
        val first = MemberName("kotlinx.coroutines.flow", "first")

        return FunSpec.builder("getSnapshot")
            .addModifiers(KModifier.SUSPEND)
            .returns(settingsClass)
            .addCode(
                "val prefs = dataStore.data.%M()\nreturn %T(%L)",
                first,
                settingsClass,
                properties.joinToString(",\n") { property ->
                    val key = property.simpleName.asString()
                    "$key = prefs[${key}Key] ?: ${getDefaultValue(property)}"
                },
            )
            .build()
    }

    private fun createSaveFunction(
        settingsClass: ClassName,
        properties: List<KSPropertyDeclaration>,
    ): FunSpec {
        val edit = MemberName("androidx.datastore.preferences.core", "edit")

        return FunSpec.builder("save")
            .addModifiers(KModifier.SUSPEND)
            .addParameter(
                "block",
                LambdaTypeName.get(
                    returnType = settingsClass,
                    parameters = listOf(
                        ParameterSpec.builder("", settingsClass).build(),
                    ),
                ),
            )
            .addCode(
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
            )
            .build()
    }

    private fun createClearFunction(): FunSpec {
        val edit = MemberName("androidx.datastore.preferences.core", "edit")

        return FunSpec.builder("clear")
            .addModifiers(KModifier.SUSPEND)
            .addCode("dataStore.%M { it.clear() }", edit)
            .build()
    }

    private fun createCompanionObject(preferenceClass: ClassName): TypeSpec =
        TypeSpec.companionObjectBuilder()
            .addAnnotation(
                AnnotationSpec.builder(ClassName("kotlinx.coroutines", "InternalCoroutinesApi"))
                    .build(),
            )
            .addProperty(
                PropertySpec.builder("instance", preferenceClass.copy(nullable = true))
                    .mutable()
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("null")
                    .build(),
            )
            .addFunction(
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
                        ).build(),
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
                .initializer(getPreferenceInitializer(property))
                .build()
        }

    // TODO: Support default values for all data types
    private fun getDefaultValue(property: KSPropertyDeclaration): String {
        val propertyType = property.type.resolve().toClassName()

        if (propertyType == Boolean::class.asClassName() &&
            property.annotations.toList()
                .isNotEmpty()
        ) {
            return property.annotations
                .firstOrNull()
                ?.arguments
                ?.firstOrNull()
                ?.value as String
        }

        // The property doesn't have the PreferenceKey annotation, use reasonable defaults
        val defaultValues = mapOf(
            Boolean::class.asClassName() to "false",
            Int::class.asClassName() to "0",
            Float::class.asClassName() to "0F",
            Long::class.asClassName() to "0L",
            Double::class.asClassName() to "0.0",
            String::class.asClassName() to "\"\"",
        )

        return defaultValues[propertyType]
            ?: throw IllegalArgumentException(
                "Unsupported type: $propertyType",
            )
    }

    private fun getPreferenceInitializer(property: KSPropertyDeclaration): CodeBlock {
        val commonImport = "androidx.datastore.preferences.core"

        val expression = when (val propertyType = property.type.resolve().toClassName()) {
            Boolean::class.asClassName() -> MemberName(commonImport, "booleanPreferencesKey")
            Int::class.asClassName() -> MemberName(commonImport, "intPreferencesKey")
            Float::class.asClassName() -> MemberName(commonImport, "floatPreferencesKey")
            Long::class.asClassName() -> MemberName(commonImport, "longPreferencesKey")
            Double::class.asClassName() -> MemberName(commonImport, "doublePreferencesKey")
            String::class.asClassName() -> MemberName(commonImport, "stringPreferencesKey")
            else -> throw IllegalArgumentException("Unsupported type: $propertyType")
        }

        return CodeBlock.builder()
            .add("%M(%S)", expression, property)
            .build()
    }
}
