package com.foreverrafs.superdiary.auth.register

/**
 * Identifies a field on the registration form.
 */
enum class Field {
    NAME,
    EMAIL,
    PASSWORD,
    VERIFY_PASSWORD,
}

/**
 * Raw input from the registration form.
 */
data class RegistrationFormData(
    val name: String,
    val email: String,
    val password: String,
    val verifyPassword: String,
)

/**
 * Error codes produced by validation rules.
 */
enum class FieldValidationError {
    REQUIRED,
    INVALID_EMAIL,
    PASSWORDS_DO_NOT_MATCH,
}

/**
 * Field-level error set returned when validation fails.
 */
data class RegistrationFormErrors(
    val nameError: FieldValidationError? = null,
    val emailError: FieldValidationError? = null,
    val passwordError: FieldValidationError? = null,
    val verifyPasswordError: FieldValidationError? = null,
) {
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || passwordError != null || verifyPasswordError != null
}

/**
 * Outcome of running the full validation suite against a [RegistrationFormData].
 */
sealed interface RegistrationFormValidationResult {
    data object Valid : RegistrationFormValidationResult
    data class Invalid(val errors: RegistrationFormErrors) : RegistrationFormValidationResult
}

// ── Rule interface ──────────────────────────────────────────────────────────

/**
 * A single, self-contained validation rule targeting one form [field].
 *
 * Implement this interface to add a new validation behaviour.  Rules are
 * assembled into the validator via constructor injection, making them easy to
 * test in isolation or swap between environments.
 *
 * Example — a minimum-length rule:
 *
 *     data class MinLengthRule(
 *         override val field: Field,
 *         private val min: Int,
 *     ) : ValidationRule {
 *         override fun validate(form: RegistrationFormData): FieldValidationError? {
 *             val value = form.value(field)
 *             return if (value.length < min) FieldValidationError.REQUIRED else null
 *         }
 *     }
 */
interface ValidationRule {
    /** The form field this rule inspects. */
    val field: Field

    /**
     * Inspects [form] and returns an error code if the rule is violated,
     * or `null` if the input passes.
     */
    fun validate(form: RegistrationFormData): FieldValidationError?
}

// ── Built-in rules ──────────────────────────────────────────────────────────

/**
 * Fails when [field] is blank (empty or whitespace-only).
 */
data class RequiredRule(override val field: Field) : ValidationRule {
    override fun validate(form: RegistrationFormData): FieldValidationError? {
        val value = form.value(field)
        return if (value.isBlank()) FieldValidationError.REQUIRED else null
    }
}

/**
 * Fails when the email field does not contain an `@` character.
 * Only fires when the value is non-blank, so it pairs naturally with [RequiredRule].
 */
data class EmailFormatRule(
    override val field: Field = Field.EMAIL,
) : ValidationRule {
    override fun validate(form: RegistrationFormData): FieldValidationError? =
        if (form.email.isNotBlank() && !form.email.contains("@")) {
            FieldValidationError.INVALID_EMAIL
        } else {
            null
        }
}

/**
 * Fails when the verify-password field differs from the password field.
 * Only fires when both values are non-blank.
 */
data class PasswordMatchRule(
    override val field: Field = Field.VERIFY_PASSWORD,
) : ValidationRule {
    override fun validate(form: RegistrationFormData): FieldValidationError? =
        if (form.verifyPassword.isNotBlank() && form.password != form.verifyPassword) {
            FieldValidationError.PASSWORDS_DO_NOT_MATCH
        } else {
            null
        }
}

// ── Validator ───────────────────────────────────────────────────────────────

/**
 * Composes a set of [ValidationRule]s and runs them against a submitted form.
 *
 * Rules are grouped by [Field]; within each group the *first* violation wins.
 * The set of rules is injectable so callers can add, remove, or replace rules
 * without modifying this class.
 *
 * Default usage:
 * ```
 * val validator = RegistrationFormValidator()
 * val result = validator.validate(formData)
 * ```
 *
 * Custom rules:
 * ```
 * val validator = RegistrationFormValidator(
 *     rules = setOf(
 *         RequiredRule(Field.NAME),
 *         EmailFormatRule(),
 *         MinLengthRule(Field.PASSWORD, min = 8),
 *     ),
 * )
 * ```
 */
class RegistrationFormValidator(
    private val rules: Set<ValidationRule> = defaultRules(),
) {
    fun validate(form: RegistrationFormData): RegistrationFormValidationResult {
        val nameError = firstErrorFor(form, Field.NAME)
        val emailError = firstErrorFor(form, Field.EMAIL)
        val passwordError = firstErrorFor(form, Field.PASSWORD)
        val verifyPasswordError = firstErrorFor(form, Field.VERIFY_PASSWORD)

        val errors = RegistrationFormErrors(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            verifyPasswordError = verifyPasswordError,
        )

        return if (errors.hasErrors) {
            RegistrationFormValidationResult.Invalid(errors)
        } else {
            RegistrationFormValidationResult.Valid
        }
    }

    private fun firstErrorFor(form: RegistrationFormData, field: Field): FieldValidationError? =
        rules
            .filter { it.field == field }
            .firstNotNullOfOrNull { it.validate(form) }

    companion object {
        /** The standard set of rules used across the application. */
        fun defaultRules(): Set<ValidationRule> = setOf(
            RequiredRule(Field.NAME),
            RequiredRule(Field.EMAIL),
            RequiredRule(Field.PASSWORD),
            RequiredRule(Field.VERIFY_PASSWORD),
            EmailFormatRule(),
            PasswordMatchRule(),
        )
    }
}

/**
 * Convenience accessor — reads the value of the given [field] from a form data instance.
 */
private fun RegistrationFormData.value(field: Field): String = when (field) {
    Field.NAME -> name
    Field.EMAIL -> email
    Field.PASSWORD -> password
    Field.VERIFY_PASSWORD -> verifyPassword
}
