package com.foreverrafs.superdiary.auth.register

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlin.test.Test

class RegistrationFormValidatorTest {

    // ── Happy path ──────────────────────────────────────────────────────────

    @Test
    fun `Should accept a valid form with all fields filled correctly`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "jane@example.com",
            password = "secret123",
            verifyPassword = "secret123",
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    // ── Name ────────────────────────────────────────────────────────────────

    @Test
    fun `Should reject when name is blank`() {
        val result = RegistrationFormValidator.validate(
            name = "",
            email = "jane@example.com",
            password = "secret123",
            verifyPassword = "secret123",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isEqualTo(FieldValidationError.REQUIRED)
    }

    // ── Email ───────────────────────────────────────────────────────────────

    @Test
    fun `Should reject when email is blank`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "",
            password = "secret123",
            verifyPassword = "secret123",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.emailError).isEqualTo(FieldValidationError.REQUIRED)
    }

    @Test
    fun `Should reject when email does not contain an at-sign`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "not-an-email",
            password = "secret123",
            verifyPassword = "secret123",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.emailError).isEqualTo(FieldValidationError.INVALID_EMAIL)
    }

    // ── Password ────────────────────────────────────────────────────────────

    @Test
    fun `Should reject when password is blank`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "jane@example.com",
            password = "",
            verifyPassword = "",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.passwordError).isEqualTo(FieldValidationError.REQUIRED)
    }

    // ── Verify password ─────────────────────────────────────────────────────

    @Test
    fun `Should reject when verify password is blank`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "jane@example.com",
            password = "secret123",
            verifyPassword = "",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.REQUIRED)
    }

    @Test
    fun `Should reject when passwords do not match`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "jane@example.com",
            password = "secret123",
            verifyPassword = "different",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.PASSWORDS_DO_NOT_MATCH)
    }

    // ── Compound cases ──────────────────────────────────────────────────────

    @Test
    fun `Should report every field as invalid when the entire form is empty`() {
        val result = RegistrationFormValidator.validate(
            name = "",
            email = "",
            password = "",
            verifyPassword = "",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isEqualTo(FieldValidationError.REQUIRED)
        assertThat(errors.emailError).isEqualTo(FieldValidationError.REQUIRED)
        assertThat(errors.passwordError).isEqualTo(FieldValidationError.REQUIRED)
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.REQUIRED)
    }

    @Test
    fun `Should report only email and verify-password when name and password are valid`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "bad",
            password = "secret123",
            verifyPassword = "mismatch",
        )

        val errors = (result as RegistrationFormValidationResult.Invalid).errors
        assertThat(errors.nameError).isEqualTo(null)
        assertThat(errors.emailError).isEqualTo(FieldValidationError.INVALID_EMAIL)
        assertThat(errors.passwordError).isEqualTo(null)
        assertThat(errors.verifyPasswordError).isEqualTo(FieldValidationError.PASSWORDS_DO_NOT_MATCH)
    }

    // ── Edge cases ──────────────────────────────────────────────────────────

    @Test
    fun `Should accept an email with subdomains in the domain part`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane Doe",
            email = "jane@student.example.co.uk",
            password = "secret123",
            verifyPassword = "secret123",
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    @Test
    fun `Should accept a name with leading and trailing whitespace`() {
        val result = RegistrationFormValidator.validate(
            name = "  Jane Doe  ",
            email = "jane@example.com",
            password = "secret123",
            verifyPassword = "secret123",
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }

    @Test
    fun `Should accept a single-word name`() {
        val result = RegistrationFormValidator.validate(
            name = "Jane",
            email = "jane@example.com",
            password = "secret123",
            verifyPassword = "secret123",
        )

        assertThat(result).isInstanceOf(RegistrationFormValidationResult.Valid::class)
    }
}
