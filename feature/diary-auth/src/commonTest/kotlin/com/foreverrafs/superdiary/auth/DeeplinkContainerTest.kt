package com.foreverrafs.superdiary.auth

import androidx.core.uri.UriUtils
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasClass
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.auth.register.DeeplinkContainer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class DeeplinkContainerTest {
    private lateinit var container: DeeplinkContainer

    @BeforeTest
    fun setup() {
        container = DeeplinkContainer()
    }

    @Test
    fun `Should create an invalid deeplink when deeplink is invalid`() = runTest {
        val invalidDeeplink =
            "superdiary://login-verify#error=access_denied&error_code=otp_expired&error_description=Email+link+is+invalid+or+has+expired"
        container.push(UriUtils.parse(invalidDeeplink))

        val link = container.pop()
        assertThat(link).isNotNull()
        assertThat(link?.type).isEqualTo(DeeplinkContainer.LinkType.Invalid)
    }

    @Test
    fun `Should create registration deeplink for registration`() = runTest {
        val registrationDeeplink =
            "superdiary://login-verify#access_token=access_token&expires_at=1735522954&expires_in=3600&refresh_token=refresh_token&token_type=bearer&type=signup"

        container.push(UriUtils.parse(registrationDeeplink))

        val link = container.pop()
        assertThat(link).isNotNull()
        assertThat(link?.type).isEqualTo(DeeplinkContainer.LinkType.Registration)
    }

    @Test
    fun `Should create magic login deeplink for magic link`() = runTest {
        val magicLinkDeeplink =
            "superdiary://login-verify#access_token=access_token&expires_at=1735522954&expires_in=3600&refresh_token=refresh_token&token_type=bearer&type=magiclink"

        container.push(UriUtils.parse(magicLinkDeeplink))

        val link = container.pop()
        assertThat(link).isNotNull()
        assertThat(link?.type).isEqualTo(DeeplinkContainer.LinkType.MagicLink)
    }

    @Test
    fun `Should create password recovery deeplink for recovery link`() = runTest {
        val recoveryDeeplink =
            "superdiary://login-verify#access_token=access_token&expires_at=1735522954&expires_in=3600&refresh_token=refresh_token&token_type=bearer&type=recovery"

        container.push(UriUtils.parse(recoveryDeeplink))

        val link = container.pop()
        assertThat(link).isNotNull()
        assertThat(link?.type).isEqualTo(DeeplinkContainer.LinkType.PasswordRecovery)
    }

    @Test
    fun `Should create email confirmation deeplink for confirmation link`() = runTest {
        val confirmationDeeplink =
            "superdiary://login-verify#access_token=access_token&expires_at=1735522954&expires_in=3600&refresh_token=refresh_token&token_type=bearer&type=confirmation"

        container.push(UriUtils.parse(confirmationDeeplink))

        val link = container.pop()
        assertThat(link).isNotNull()
        assertThat(link?.type).isEqualTo(DeeplinkContainer.LinkType.EmailConfirmation)
    }

    @Test
    fun `Should only allow a single deeplink to be added`() = runTest {
        val invalidDeeplink =
            "superdiary://login-verify#error=access_denied&error_code=otp_expired&error_description=Email+link+is+invalid+or+has+expired"

        container.push(UriUtils.parse(invalidDeeplink))

        assertFailure {
            container.push(UriUtils.parse(invalidDeeplink))
        }.hasClass(IllegalArgumentException::class)
    }

    @Test
    fun `Should clear container when deeplink is accessed`() = runTest {
        val invalidDeeplink =
            "superdiary://login-verify#error=access_denied&error_code=otp_expired&error_description=Email+link+is+invalid+or+has+expired"

        container.push(UriUtils.parse(invalidDeeplink))

        // Extract the link from the container
        val deepLink = container.pop()

        assertThat(deepLink).isNotNull()

        // attempt to extract the link again from the container
        val deepLink2 = container.pop()

        assertThat(deepLink2).isNull()
    }

    @Test
    fun `Peeking the container should not delete the deeplink`() = runTest {
        val invalidDeeplink =
            "superdiary://login-verify#error=access_denied&error_code=otp_expired&error_description=Email+link+is+invalid+or+has+expired"

        container.push(UriUtils.parse(invalidDeeplink))

        // Just take a peek at the item without popping it
        val deepLink = container.peek()

        assertThat(deepLink).isNotNull()

        // Pop the item this time, removing it from the container
        val deepLink2 = container.pop()

        assertThat(deepLink2).isNotNull()
    }

    @Test
    fun `Should throw an error for invalid links`() = runTest {
        val invalidLink = UriUtils.parse("https://www.google.com")
        assertFailure {
            container.push(invalidLink)
        }.hasClass(IllegalArgumentException::class)
    }
}
