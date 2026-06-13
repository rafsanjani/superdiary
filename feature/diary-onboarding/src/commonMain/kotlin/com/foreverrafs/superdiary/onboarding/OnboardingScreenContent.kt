package com.foreverrafs.superdiary.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.design.components.BrandLogo
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
import superdiary.feature.diary_onboarding.generated.resources.onboarding_relive_body
import superdiary.feature.diary_onboarding.generated.resources.onboarding_relive_chip
import superdiary.feature.diary_onboarding.generated.resources.onboarding_relive_title
import superdiary.feature.diary_onboarding.generated.resources.onboarding_skip

private val Paper = Color(0xFFFFFCF8)
private val Ink = Color(0xFF141319)
private val MutedInk = Color(0xFF6E6876)
private val SoftLilac = Color(0xFFF1ECFF)
private val SoftPeach = Color(0xFFFFEEE5)
private val AccentOrange = Color(0xFFFF7A3D)
private val AccentPurple = Color(0xFF5B4AD8)
private val AccentMint = Color(0xFF55C7A7)
private val ShadowTint = Color(0x1F35264D)

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Paper)
                .padding(paddingValues),
        ) {
            OrganicBackground()

            TextButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 20.dp)
                    .testTag("onboarding_skip"),
                onClick = onComplete,
                enabled = !isFinalPage,
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_skip),
                    color = if (isFinalPage) Color.Transparent else MutedInk,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Wordmark()

                Spacer(Modifier.height(18.dp))

                OnboardingIllustration(
                    page = page,
                    selectedPageIndex = selectedPageIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )

                OnboardingCopy(
                    page = page,
                    pageCount = pages.size,
                    selectedPageIndex = selectedPageIndex,
                )

                Spacer(Modifier.height(26.dp))

                PageIndicators(
                    pageCount = pages.size,
                    selectedPageIndex = selectedPageIndex,
                )

                Spacer(Modifier.height(24.dp))

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

@Composable
private fun Wordmark(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        BrandLogo(modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = "SuperDiary.",
            color = Ink,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun BoxScope.OrganicBackground() {
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = (-72).dp, y = 88.dp)
            .size(190.dp)
            .clip(CircleShape)
            .background(SoftPeach.copy(alpha = 0.55f)),
    )
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 88.dp, y = 188.dp)
            .size(220.dp)
            .clip(CircleShape)
            .background(SoftLilac.copy(alpha = 0.70f)),
    )
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = (-92).dp, y = 36.dp)
            .size(210.dp)
            .clip(CircleShape)
            .background(Color(0xFFEAF7F1).copy(alpha = 0.72f)),
    )
}

@Composable
private fun OnboardingIllustration(
    page: OnboardingPage,
    selectedPageIndex: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        TravelLineCanvas(modifier = Modifier.fillMaxSize())
        DottedCluster(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 34.dp, y = 86.dp),
            color = AccentOrange,
        )
        DottedCluster(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-8).dp, y = (-28).dp),
            color = AccentPurple,
        )
        DottedCluster(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 58.dp, y = (-52).dp),
            color = AccentMint,
        )

        when (page.title) {
            OnboardingPageTitle.CaptureYourDay -> CaptureYourDayVisual(selectedPageIndex)
            OnboardingPageTitle.FindPatterns -> FindPatternsVisual(selectedPageIndex)
            OnboardingPageTitle.ReliveMoments -> ReliveMomentsVisual(selectedPageIndex)
            OnboardingPageTitle.PrivateReflection -> PrivateReflectionVisual(selectedPageIndex)
        }
    }
}

@Composable
private fun TravelLineCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.07f, size.height * 0.62f)
            cubicTo(
                size.width * 0.20f,
                size.height * 0.17f,
                size.width * 0.54f,
                size.height * 0.23f,
                size.width * 0.48f,
                size.height * 0.48f,
            )
            cubicTo(
                size.width * 0.38f,
                size.height * 0.82f,
                size.width * 0.84f,
                size.height * 0.80f,
                size.width * 0.92f,
                size.height * 0.42f,
            )
        }
        drawPath(
            path = path,
            color = Color(0xFFE7E0DA),
            style = Stroke(
                width = 3.2f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f),
            ),
        )
    }
}

@Composable
private fun DottedCluster(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(width = 42.dp, height = 28.dp)) {
        val radius = 3.2f
        repeat(3) { row ->
            repeat(4) { column ->
                drawCircle(
                    color = color.copy(alpha = if ((row + column) % 2 == 0) 0.90f else 0.42f),
                    radius = radius,
                    center = Offset(
                        x = 7f + column * 10f + row * 3f,
                        y = 6f + row * 8f,
                    ),
                )
            }
        }
    }
}

@Composable
private fun CaptureYourDayVisual(selectedPageIndex: Int) {
    Box(contentAlignment = Alignment.Center) {
        FloatingMemoryCard(
            modifier = Modifier
                .offset(x = (-70).dp, y = (-34).dp)
                .width(128.dp),
            eyebrow = "Today",
            title = "Coffee with Sara",
            body = "11:42 • Shoreditch",
            accent = AccentOrange,
        )
        MiniMoodPill(
            modifier = Modifier.offset(x = 72.dp, y = (-84).dp),
            emoji = "✨",
            text = "+12 moments",
            color = AccentOrange,
        )
        TimelineCard(
            modifier = Modifier
                .offset(x = 34.dp, y = 34.dp)
                .width(190.dp),
            selectedPageIndex = selectedPageIndex,
        )
        PhotoBubble(
            modifier = Modifier.offset(x = (-88).dp, y = 94.dp),
            colors = listOf(Color(0xFFFFB36B), Color(0xFFFF6C8A)),
        )
    }
}

@Composable
private fun FindPatternsVisual(selectedPageIndex: Int) {
    Box(contentAlignment = Alignment.Center) {
        SoftMapSurface()
        PatternPhotoStack()
        MiniMoodPill(
            modifier = Modifier.offset(x = 86.dp, y = 62.dp),
            emoji = "📍",
            text = "Most mindful on Sundays",
            color = AccentPurple,
        )
        SparkCard(
            modifier = Modifier.offset(x = (-76).dp, y = 68.dp),
            label = "+32% calm",
            color = AccentMint,
        )
        PageNumberBadge(selectedPageIndex)
    }
}

@Composable
private fun ReliveMomentsVisual(selectedPageIndex: Int) {
    Box(contentAlignment = Alignment.Center) {
        FloatingMemoryCard(
            modifier = Modifier
                .offset(x = (-18).dp, y = (-78).dp)
                .width(230.dp),
            eyebrow = "Memory lane",
            title = "The evening everything felt lighter",
            body = "Tap to replay photos, notes and places",
            accent = AccentPurple,
        )
        FloatingListItem(
            modifier = Modifier.offset(x = 0.dp, y = 4.dp),
            title = "Rainy walk by the river",
            subtitle = "4 photos • 2 notes",
            color = AccentMint,
        )
        FloatingListItem(
            modifier = Modifier.offset(x = 18.dp, y = 78.dp),
            title = "First week at the new job",
            subtitle = "Confidence was building",
            color = AccentOrange,
        )
        PageNumberBadge(selectedPageIndex, modifier = Modifier.offset(x = (-96).dp, y = 88.dp))
    }
}

@Composable
private fun PrivateReflectionVisual(selectedPageIndex: Int) {
    Box(contentAlignment = Alignment.Center) {
        SecurityRing()
        FloatingMemoryCard(
            modifier = Modifier
                .offset(x = 0.dp, y = (-18).dp)
                .width(226.dp),
            eyebrow = "Private space",
            title = "Only your story, only your eyes",
            body = "Reflect honestly with a diary designed around trust.",
            accent = AccentMint,
        )
        MiniMoodPill(
            modifier = Modifier.offset(x = (-78).dp, y = 90.dp),
            emoji = "🔒",
            text = "Encrypted by design",
            color = AccentMint,
        )
        SparkCard(
            modifier = Modifier.offset(x = 96.dp, y = 80.dp),
            label = "No noise",
            color = AccentPurple,
        )
        PageNumberBadge(selectedPageIndex, modifier = Modifier.offset(x = 100.dp, y = (-102).dp))
    }
}

@Composable
private fun FloatingMemoryCard(
    eyebrow: String,
    title: String,
    body: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.shadow(22.dp, RoundedCornerShape(26.dp), ambientColor = ShadowTint, spotColor = ShadowTint),
        color = Color.White,
        shape = RoundedCornerShape(26.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 42.dp, height = 38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(accent.copy(alpha = 0.85f), SoftPeach))),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = eyebrow,
                color = accent,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                color = Ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 17.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = body,
                color = MutedInk,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
private fun TimelineCard(
    selectedPageIndex: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.shadow(26.dp, RoundedCornerShape(24.dp), ambientColor = ShadowTint, spotColor = ShadowTint),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            TimelineRow("🇬🇧", "Morning reflections", "8 mins")
            TimelineRow("🌿", "Walked through the park", "14 mins")
            TimelineRow("💬", "Conversation worth saving", "3 mins")
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                PageNumberBadge(selectedPageIndex)
            }
        }
    }
}

@Composable
private fun TimelineRow(
    emoji: String,
    title: String,
    time: String,
) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Ink,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = time,
                color = MutedInk,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun MiniMoodPill(
    emoji: String,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.shadow(16.dp, RoundedCornerShape(100), ambientColor = ShadowTint, spotColor = ShadowTint),
        color = Color.White,
        shape = RoundedCornerShape(100),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(Modifier.width(7.dp))
            Text(
                text = text,
                color = Ink,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(Modifier.width(7.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Composable
private fun PhotoBubble(
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(58.dp)
            .shadow(18.dp, CircleShape, ambientColor = ShadowTint, spotColor = ShadowTint)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors))
            .border(5.dp, Color.White, CircleShape),
    )
}

@Composable
private fun SoftMapSurface() {
    Canvas(
        modifier = Modifier
            .size(width = 286.dp, height = 205.dp)
            .clip(RoundedCornerShape(42.dp)),
    ) {
        repeat(16) { index ->
            val row = index / 4
            val column = index % 4
            drawOval(
                color = Color(0xFFE5E0ED).copy(alpha = 0.40f),
                topLeft = Offset(column * 72f + row * 11f, row * 37f + column * 6f),
                size = Size(70f, 34f),
            )
        }
    }
}

@Composable
private fun PatternPhotoStack() {
    Box(contentAlignment = Alignment.Center) {
        PhotoTile(
            modifier = Modifier.offset(x = (-62).dp, y = (-46).dp),
            colors = listOf(Color(0xFF474553), Color(0xFFE9B4A0)),
            rotationHint = -1,
        )
        PhotoTile(
            modifier = Modifier.offset(x = 0.dp, y = (-58).dp),
            colors = listOf(Color(0xFF8AC7FF), Color(0xFF8AD96E)),
            isFeatured = true,
            rotationHint = 0,
        )
        PhotoTile(
            modifier = Modifier.offset(x = 64.dp, y = (-44).dp),
            colors = listOf(Color(0xFF4E6A75), Color(0xFFFFD185)),
            rotationHint = 1,
        )
    }
}

@Composable
private fun PhotoTile(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    isFeatured: Boolean = false,
    rotationHint: Int = 0,
) {
    Box(
        modifier = modifier
            .size(width = if (isFeatured) 76.dp else 52.dp, height = if (isFeatured) 96.dp else 70.dp)
            .shadow(18.dp, RoundedCornerShape(18.dp), ambientColor = ShadowTint, spotColor = ShadowTint)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(colors))
            .border(
                width = if (isFeatured) 4.dp else 0.dp,
                color = if (isFeatured) AccentOrange else Color.Transparent,
                shape = RoundedCornerShape(18.dp),
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .offset(y = 12.dp)
                .size(if (isFeatured) 30.dp else 22.dp)
                .clip(CircleShape)
                .background(if (rotationHint < 0) SoftPeach else SoftLilac)
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = if (isFeatured) "🙂" else "•", fontSize = if (isFeatured) 16.sp else 12.sp)
        }
    }
}

@Composable
private fun FloatingListItem(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .widthIn(max = 278.dp)
            .shadow(18.dp, RoundedCornerShape(24.dp), ambientColor = ShadowTint, spotColor = ShadowTint),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(color.copy(alpha = 0.92f), SoftPeach))),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = MutedInk,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun SparkCard(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.shadow(14.dp, RoundedCornerShape(18.dp), ambientColor = ShadowTint, spotColor = ShadowTint),
        color = color,
        contentColor = Color.White,
        shape = RoundedCornerShape(18.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            text = label,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun SecurityRing() {
    Canvas(modifier = Modifier.size(260.dp)) {
        drawCircle(
            color = SoftLilac.copy(alpha = 0.70f),
            radius = size.minDimension * 0.39f,
            style = Stroke(width = 26f),
        )
        drawArc(
            color = AccentMint,
            startAngle = -32f,
            sweepAngle = 105f,
            useCenter = false,
            style = Stroke(width = 27f, cap = StrokeCap.Round),
        )
        drawArc(
            color = AccentOrange,
            startAngle = 156f,
            sweepAngle = 54f,
            useCenter = false,
            style = Stroke(width = 18f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun PageNumberBadge(
    selectedPageIndex: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Ink,
        contentColor = Color.White,
        shape = RoundedCornerShape(100),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = "0${selectedPageIndex + 1}",
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun OnboardingCopy(
    page: OnboardingPage,
    pageCount: Int,
    selectedPageIndex: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FeatureChip(page.title)

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.title.asText(),
            color = Ink,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            lineHeight = 38.sp,
            letterSpacing = (-0.7).sp,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            modifier = Modifier.widthIn(max = 310.dp),
            text = page.body.asText(),
            color = MutedInk,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "${selectedPageIndex + 1} / $pageCount",
            color = MutedInk.copy(alpha = 0.62f),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
        )
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
        color = AccentOrange.copy(alpha = 0.10f),
        contentColor = AccentOrange,
        shape = RoundedCornerShape(100),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
            text = chipText,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.labelSmall,
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
                    .size(if (isSelected) 9.dp else 7.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AccentPurple else Color(0xFFE5E0EA))
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
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .height(58.dp)
                .shadow(20.dp, RoundedCornerShape(22.dp), ambientColor = AccentOrange.copy(alpha = 0.22f), spotColor = AccentOrange.copy(alpha = 0.22f))
                .testTag(if (isFinalPage) "onboarding_get_started" else "onboarding_next"),
            onClick = if (isFinalPage) onComplete else onNext,
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentOrange,
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = stringResource(if (isFinalPage) Res.string.onboarding_get_started else Res.string.onboarding_next),
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            modifier = Modifier.testTag("onboarding_back"),
            onClick = onBack,
            enabled = !isFirstPage,
        ) {
            Text(
                text = stringResource(Res.string.onboarding_back),
                color = if (isFirstPage) Color.Transparent else MutedInk,
                fontWeight = FontWeight.SemiBold,
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
