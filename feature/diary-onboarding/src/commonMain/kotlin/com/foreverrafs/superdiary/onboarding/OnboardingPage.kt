package com.foreverrafs.superdiary.onboarding

enum class OnboardingPageTitle {
    CaptureYourDay,
    FindPatterns,
    ReliveMoments,
    PrivateReflection,
}

enum class OnboardingPageBody {
    CaptureYourDay,
    FindPatterns,
    ReliveMoments,
    PrivateReflection,
}

data class OnboardingPage(
    val title: OnboardingPageTitle,
    val body: OnboardingPageBody,
) {
    companion object {
        val defaultPages = listOf(
            OnboardingPage(
                title = OnboardingPageTitle.CaptureYourDay,
                body = OnboardingPageBody.CaptureYourDay,
            ),
            OnboardingPage(
                title = OnboardingPageTitle.FindPatterns,
                body = OnboardingPageBody.FindPatterns,
            ),
            OnboardingPage(
                title = OnboardingPageTitle.ReliveMoments,
                body = OnboardingPageBody.ReliveMoments,
            ),
            OnboardingPage(
                title = OnboardingPageTitle.PrivateReflection,
                body = OnboardingPageBody.PrivateReflection,
            ),
        )
    }
}
