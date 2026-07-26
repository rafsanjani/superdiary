package com.foreverrafs.superdiary.onboarding

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import kotlin.test.Test

class OnboardingPageTest {
    @Test
    fun `Should expose polished onboarding pages in display order`() {
        val pages = OnboardingPage.defaultPages

        assertThat(pages).hasSize(3)
        assertThat(pages.first().title).isEqualTo(OnboardingPageTitle.CaptureYourDay)
        assertThat(pages.last().title).isEqualTo(OnboardingPageTitle.KeepEveryMemoryClose)
    }

    @Test
    fun `Should provide all bundled images for every onboarding page`() {
        OnboardingPage.defaultPages.forEach { page ->
            assertThat(page.images).hasSize(6)
        }
    }
}
