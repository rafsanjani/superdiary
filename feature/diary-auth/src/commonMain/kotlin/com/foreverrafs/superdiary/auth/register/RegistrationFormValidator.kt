package com.foreverrafs.superdiary.auth.register

/**
 * Pure validation logic for the registration form.
 *
 * All validation rules are expressed as a single function that takes raw form
 * input and returns either [Valid] or [Invalid] with field-level error codes.
 *
 * The validator has no dependencies on Android, Compose, coroutines, or any
 * framework — it is a pure function and trivially testable.
 */
object RegistrationFormValidator {

    fun validate(
        name: String,
        email: String,
        password: String,
        verifyPassword: String,
    ): RegistrationFormValidationResult {
        val nameError = validateName(name)
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        val verifyPasswordError = validateVerifyPassword(password, verifyPassword)

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

    private fun validateName(name: String): FieldValidationError? {
        if (name.isBlank()) return FieldValidationError.REQUIRED
        return null
    }

    private fun validateEmail(email: String): FieldValidationError? {
        if (email.isBlank()) return FieldValidationError.REQUIRED
        if (!email.contains("@")) return FieldValidationError.INVALID_EMAIL
        return null
    }

    private fun validatePassword(password: String): FieldValidationError? {
        if (password.isBlank()) return FieldValidationError.REQUIRED
        return null
    }

    private fun validateVerifyPassword(
        password: String,
        verifyPassword: String,
    ): FieldValidationError? {
        if (verifyPassword.isBlank()) return FieldValidationError.REQUIRED
        if (verifyPassword != password) return FieldValidationError.PASSWORDS_DO_NOT_MATCH
        return null
    }
}

enum class FieldValidationError {
    REQUIRED,
    INVALID_EMAIL,
    PASSWORDS_DO_NOT_MATCH,
}

data class RegistrationFormErrors(
    val nameError: FieldValidationError? = null,
    val emailError: FieldValidationError? = null,
    val passwordError: FieldValidationError? = null,
    val verifyPasswordError: FieldValidationError? = null,
) {
    val hasErrors: Boolean
        get() = nameError != null || emailError != null || passwordError != null || verifyPasswordError != null
}

sealed interface RegistrationFormValidationResult {
    data object Valid : RegistrationFormValidationResult
    data class Invalid(val errors: RegistrationFormErrors) : RegistrationFormValidationResult
}
