package com.foreverrafs.superdiary.onboarding

import org.jetbrains.compose.resources.DrawableResource
import superdiary.feature.diary_onboarding.generated.resources.Res
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memory_1
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memory_2
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memory_3
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memory_4
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memory_5
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memory_6

enum class OnboardingPageTitle {
    CaptureYourDay,
    UnderstandYourPatterns,
    KeepEveryMemoryClose,
}

data class OnboardingPage(
    val title: OnboardingPageTitle,
    val headline: String,
    val body: String,
    val images: List<DrawableResource>,
) {
    companion object {
        private val diaryImages = listOf(
            Res.drawable.onboarding_memory_1,
            Res.drawable.onboarding_memory_2,
            Res.drawable.onboarding_memory_3,
            Res.drawable.onboarding_memory_4,
            Res.drawable.onboarding_memory_5,
            Res.drawable.onboarding_memory_6,
        )

        val defaultPages = listOf(
            OnboardingPage(
                title = OnboardingPageTitle.CaptureYourDay,
                headline = "Every day becomes a story worth keeping",
                body = "Capture notes, photos, places, and the little moments that make each day yours.",
                images = diaryImages,
            ),
            OnboardingPage(
                title = OnboardingPageTitle.UnderstandYourPatterns,
                headline = "Understand the rhythm behind your days",
                body = "Gentle reflections reveal patterns in your moods, habits, and personal growth.",
                images = diaryImages.drop(2) + diaryImages.take(2),
            ),
            OnboardingPage(
                title = OnboardingPageTitle.KeepEveryMemoryClose,
                headline = "Keep every meaningful moment close",
                body = "Find old memories in seconds and keep your private story safely within reach.",
                images = diaryImages.drop(4) + diaryImages.take(4),
            ),
        )
    }
}
