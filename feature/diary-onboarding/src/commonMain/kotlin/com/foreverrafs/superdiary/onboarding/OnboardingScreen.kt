package com.foreverrafs.superdiary.onboarding

import androidx.compose.runtime.Composable

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
) {
    OnboardingScreenContent(
        onComplete = onComplete,
    )
}
