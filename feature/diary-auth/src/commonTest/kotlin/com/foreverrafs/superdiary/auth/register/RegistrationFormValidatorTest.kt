package com.foreverrafs.superdiary.auth.register

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import kotlin.test.Test

class RegistrationFormValidatorTest {

    private val validator = RegistrationFormValidator()

    // ── Happy path ──────────────────────────────────────────────────────────

    @Test
    fun `Should accept a valid form with all fields filled correctly`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    // ── Name ────────────────────────────────────────────────────────────────

    @Test
    fun `Should reject when name is blank`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isEqualTo(FieldValidationError.Required)
    }

    // ── Email ───────────────────────────────────────────────────────────────

    @Test
    fun `Should reject when email is blank`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.emailError).isEqualTo(FieldValidationError.Required)
    }

    @Test
    fun `Should reject when email does not contain an at-sign`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "not-an-email",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.emailError).isEqualTo(FieldValidationError.InvalidEmail)
    }

    // ── Password ────────────────────────────────────────────────────────────

    @Test
    fun `Should reject when password is blank`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "",
                verifyPassword = "",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.passwordError).isEqualTo(FieldValidationError.Required)
    }

    // ── Verify password ─────────────────────────────────────────────────────

    @Test
    fun `Should reject when verify password is blank`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.Required)
    }

    @Test
    fun `Should reject when passwords do not match`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "different",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.PasswordsDoNotMatch)
    }

    // ── Compound cases ──────────────────────────────────────────────────────

    @Test
    fun `Should report every field as invalid when the entire form is empty`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "",
                verifyPassword = "",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isEqualTo(FieldValidationError.Required)
        assertThat(errors.emailError).isEqualTo(FieldValidationError.Required)
        assertThat(errors.passwordError).isEqualTo(FieldValidationError.Required)
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.Required)
    }

    @Test
    fun `Should report only email and verify-password when name and password are valid`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "bad",
                password = "secret123",
                verifyPassword = "mismatch",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isNull()
        assertThat(errors.emailError).isEqualTo(FieldValidationError.InvalidEmail)
        assertThat(errors.passwordError).isNull()
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.PasswordsDoNotMatch)
    }

    // ── Edge cases ──────────────────────────────────────────────────────────

    @Test
    fun `Should accept an email with subdomains in the domain part`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane Doe",
                email = "jane@student.example.co.uk",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    @Test
    fun `Should accept a name with leading and trailing whitespace`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "  Jane Doe  ",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    @Test
    fun `Should accept a single-word name`() {
        val result = validator.validate(
            RegistrationFormData(
                name = "Jane",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    // ── Rule-isolation tests ────────────────────────────────────────────────

    @Test
    fun `RequiredRule should reject a blank value`() {
        val rule = RequiredRule(Field.NAME)

        val result = rule.validate(
            RegistrationFormData(
                name = "",
                email = "jane@example.com",
                password = "secret123",
                verifyPassword = "secret123",
            ),
        )

        assertThat(result).isEqualTo(FieldValidationError.Required)
    }

    @Test
    fun `RequiredRule should accept a non-blank value`() {
        val rule = RequiredRule(Field.NAME)

        val result = rule.validate(
            RegistrationFormData(
                name = "Jane",
                email = "",
                password = "",
                verifyPassword = "",
            ),
        )

        assertThat(result).isNull()
    }

    @Test
    fun `EmailFormatRule should reject an email without an at-sign`() {
        val rule = EmailFormatRule()

        val result = rule.validate(
            RegistrationFormData(
                name = "",
                email = "not-an-email",
                password = "",
                verifyPassword = "",
            ),
        )

        assertThat(result).isEqualTo(FieldValidationError.InvalidEmail)
    }

    @Test
    fun `EmailFormatRule should not fire on a blank email`() {
        val rule = EmailFormatRule()

        val result = rule.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "",
                verifyPassword = "",
            ),
        )

        assertThat(result).isNull()
    }

    @Test
    fun `PasswordMatchRule should reject mismatched passwords`() {
        val rule = PasswordMatchRule()

        val result = rule.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "abc123",
                verifyPassword = "xyz789",
            ),
        )

        assertThat(result).isEqualTo(FieldValidationError.PasswordsDoNotMatch)
    }

    @Test
    fun `PasswordMatchRule should not fire when verify password is blank`() {
        val rule = PasswordMatchRule()

        val result = rule.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "abc123",
                verifyPassword = "",
            ),
        )

        assertThat(result).isNull()
    }

    @Test
    fun `PasswordMatchRule should accept matching passwords`() {
        val rule = PasswordMatchRule()

        val result = rule.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "abc123",
                verifyPassword = "abc123",
            ),
        )

        assertThat(result).isNull()
    }

    // ── Custom rule composition ─────────────────────────────────────────────

    @Test
    fun `Should validate only the rules that were added`() {
        val customValidator = RegistrationFormValidator().apply {
            clearRules()
            addRule(RequiredRule(Field.EMAIL))
            addRule(EmailFormatRule())
        }

        // Only email is validated — name, password etc. are ignored
        val result = customValidator.validate(
            RegistrationFormData(
                name = "",
                email = "bad",
                password = "",
                verifyPassword = "",
            ),
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isNull()
        assertThat(errors.emailError).isEqualTo(FieldValidationError.InvalidEmail)
        assertThat(errors.passwordError).isNull()
        assertThat(errors.verifyPasswordError).isNull()
    }

    @Test
    fun `Should accept a validator with no rules`() {
        val noOpValidator = RegistrationFormValidator().apply { clearRules() }

        val result = noOpValidator.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "",
                verifyPassword = "",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    @Test
    fun `First error wins when multiple rules target the same field`() {
        val customValidator = RegistrationFormValidator().apply {
            clearRules()
            addRule(RequiredRule(Field.NAME))
        }

        val result = customValidator.validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "",
                verifyPassword = "",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Invalid::class)
    }

    // ── Default rules ───────────────────────────────────────────────────────

    @Test
    fun `Default validator should reject an empty form`() {
        val result = RegistrationFormValidator().validate(
            RegistrationFormData(
                name = "",
                email = "",
                password = "",
                verifyPassword = "",
            ),
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Invalid::class)
    }
}
