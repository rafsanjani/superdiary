package com.foreverrafs.superdiary.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OnboardingScreenContent(
        onComplete = onComplete,
        modifier = modifier,
    )
}
