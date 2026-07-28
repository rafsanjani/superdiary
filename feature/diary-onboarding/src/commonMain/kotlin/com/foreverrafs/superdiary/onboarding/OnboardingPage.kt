package com.foreverrafs.superdiary.onboarding

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import superdiary.feature.diary_onboarding.generated.resources.Res
import superdiary.feature.diary_onboarding.generated.resources.onboarding_capture_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_capture_headline
import superdiary.feature.diary_onboarding.generated.resources.onboarding_insights_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_insights_headline
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memories_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_memories_headline
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
    val headline: StringResource,
    val body: StringResource,
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
                headline = Res.string.onboarding_capture_headline,
                body = Res.string.onboarding_capture_body,
                images = diaryImages,
            ),
            OnboardingPage(
                title = OnboardingPageTitle.UnderstandYourPatterns,
                headline = Res.string.onboarding_insights_headline,
                body = Res.string.onboarding_insights_body,
                images = diaryImages.drop(2) + diaryImages.take(2),
            ),
            OnboardingPage(
                title = OnboardingPageTitle.KeepEveryMemoryClose,
                headline = Res.string.onboarding_memories_headline,
                body = Res.string.onboarding_memories_body,
                images = diaryImages.drop(4) + diaryImages.take(4),
            ),
        )
    }
}
