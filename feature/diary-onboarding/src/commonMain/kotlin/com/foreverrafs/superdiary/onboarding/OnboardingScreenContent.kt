package com.foreverrafs.superdiary.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.design.components.BrandLogo
import com.foreverrafs.superdiary.design.components.PrimaryButton
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_onboarding.generated.resources.Res
import superdiary.feature.diary_onboarding.generated.resources.onboarding_back
import superdiary.feature.diary_onboarding.generated.resources.onboarding_capture_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_capture_chip
import superdiary.feature.diary_onboarding.generated.resources.onboarding_capture_title
import superdiary.feature.diary_onboarding.generated.resources.onboarding_get_started
import superdiary.feature.diary_onboarding.generated.resources.onboarding_next
import superdiary.feature.diary_onboarding.generated.resources.onboarding_patterns_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_patterns_chip
import superdiary.feature.diary_onboarding.generated.resources.onboarding_patterns_title
import superdiary.feature.diary_onboarding.generated.resources.onboarding_private_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_private_chip
import superdiary.feature.diary_onboarding.generated.resources.onboarding_private_title
import superdiary.feature.diary_onboarding.generated.resources.onboarding_progress
import superdiary.feature.diary_onboarding.generated.resources.onboarding_relive_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_relive_chip
import superdiary.feature.diary_onboarding.generated.resources.onboarding_relive_title
import superdiary.feature.diary_onboarding.generated.resources.onboarding_skip

@Composable
fun OnboardingScreenContent(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    pages: List<OnboardingPage> = OnboardingPage.defaultPages,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    val lastPageIndex = pages.lastIndex
    val page = pages.getOrNull(selectedPageIndex) ?: return
    val isFinalPage = selectedPageIndex == lastPageIndex

    Scaffold(modifier = modifier.testTag("onboarding_screen")) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                            ),
                        ),
                    ),
            ) {
                TextButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 24.dp, end = 24.dp)
                        .testTag("onboarding_skip"),
                    onClick = onComplete,
                    enabled = !isFinalPage,
                ) {
                    Text(
                        text = stringResource(Res.string.onboarding_skip),
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Spacer(Modifier.height(16.dp))

                    OnboardingHero(
                        page = page,
                        selectedPageIndex = selectedPageIndex,
                        pageCount = pages.size,
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        PageIndicators(
                            pageCount = pages.size,
                            selectedPageIndex = selectedPageIndex,
                        )

                        Spacer(Modifier.height(28.dp))

                        OnboardingControls(
                            isFirstPage = selectedPageIndex == 0,
                            isFinalPage = isFinalPage,
                            onBack = { selectedPageIndex = (selectedPageIndex - 1).coerceAtLeast(0) },
                            onNext = { selectedPageIndex = (selectedPageIndex + 1).coerceAtMost(lastPageIndex) },
                            onComplete = onComplete,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingHero(
    page: OnboardingPage,
    selectedPageIndex: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.30f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                            Color.Transparent,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            BrandLogo(modifier = Modifier.size(84.dp))
        }

        Spacer(Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        Res.string.onboarding_progress,
                        selectedPageIndex + 1,
                        pageCount,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = page.title.asText(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = page.body.asText(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                )

                Spacer(Modifier.height(24.dp))

                FeatureChip(page.title)
            }
        }
    }
}

@Composable
private fun FeatureChip(title: OnboardingPageTitle) {
    val chipText = when (title) {
        OnboardingPageTitle.CaptureYourDay -> stringResource(Res.string.onboarding_capture_chip)
        OnboardingPageTitle.FindPatterns -> stringResource(Res.string.onboarding_patterns_chip)
        OnboardingPageTitle.ReliveMoments -> stringResource(Res.string.onboarding_relive_chip)
        OnboardingPageTitle.PrivateReflection -> stringResource(Res.string.onboarding_private_chip)
    }

    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(100),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            text = chipText,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    selectedPageIndex: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == selectedPageIndex
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(if (isSelected) 28.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        },
                    )
                    .testTag("onboarding_indicator_$index"),
            )
        }
    }
}

@Composable
private fun OnboardingControls(
    isFirstPage: Boolean,
    isFinalPage: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("onboarding_back"),
            onClick = onBack,
            enabled = !isFirstPage,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                disabledContentColor = Color.Transparent,
            ),
        ) {
            Text(text = stringResource(Res.string.onboarding_back))
        }

        if (isFinalPage) {
            PrimaryButton(
                modifier = Modifier
                    .weight(1.45f)
                    .testTag("onboarding_get_started"),
                text = stringResource(Res.string.onboarding_get_started),
                onClick = onComplete,
            )
        } else {
            PrimaryButton(
                modifier = Modifier
                    .weight(1.45f)
                    .testTag("onboarding_next"),
                text = stringResource(Res.string.onboarding_next),
                onClick = onNext,
            )
        }
    }
}

@Composable
private fun OnboardingPageTitle.asText(): String = when (this) {
    OnboardingPageTitle.CaptureYourDay -> stringResource(Res.string.onboarding_capture_title)
    OnboardingPageTitle.FindPatterns -> stringResource(Res.string.onboarding_patterns_title)
    OnboardingPageTitle.ReliveMoments -> stringResource(Res.string.onboarding_relive_title)
    OnboardingPageTitle.PrivateReflection -> stringResource(Res.string.onboarding_private_title)
}

@Composable
private fun OnboardingPageBody.asText(): String = when (this) {
    OnboardingPageBody.CaptureYourDay -> stringResource(Res.string.onboarding_capture_body)
    OnboardingPageBody.FindPatterns -> stringResource(Res.string.onboarding_patterns_body)
    OnboardingPageBody.ReliveMoments -> stringResource(Res.string.onboarding_relive_body)
    OnboardingPageBody.PrivateReflection -> stringResource(Res.string.onboarding_private_body)
}

@Preview
@Composable
private fun OnboardingScreenContentPreview() {
    SuperDiaryPreviewTheme {
        OnboardingScreenContent(onComplete = {})
    }
}
