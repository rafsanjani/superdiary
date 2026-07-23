package com.foreverrafs.superdiary.onboarding

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import kotlin.test.Test

class OnboardingPageTest {
    @Test
    fun `Should expose the onboarding pages in display order`() {
        val pages = OnboardingPage.defaultPages

        assertThat(pages).hasSize(4)
        assertThat(pages.first().title).isEqualTo(OnboardingPageTitle.CaptureYourDay)
        assertThat(pages.last().title).isEqualTo(OnboardingPageTitle.PrivateReflection)
    }
}
