package com.foreverrafs.superdiary.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import superdiary.feature.diary_onboarding.generated.resources.Res
import superdiary.feature.diary_onboarding.generated.resources.onboarding_get_started
import superdiary.feature.diary_onboarding.generated.resources.onboarding_next
import superdiary.feature.diary_onboarding.generated.resources.onboarding_skip

private val Ink = Color(0xFF17120F)
private val MutedInk = Color(0xFF6E6965)
private val Canvas = Color(0xFFFCFCFB)
private val Peach = Color(0xFFFFE1CF)
private val SoftPeach = Color(0xFFFFF4ED)
private val Sage = Color(0xFFB8D4CB)
private val SoftBlue = Color(0xFFEAF1F4)
private val Track = Color(0xFFE7E3E0)

@Composable
fun OnboardingScreenContent(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    pages: List<OnboardingPage> = OnboardingPage.defaultPages,
) {
    var selectedPageIndex by rememberSaveable { mutableIntStateOf(0) }
    val lastPageIndex = pages.lastIndex
    val isFinalPage = selectedPageIndex == lastPageIndex

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Canvas)
            .testTag("onboarding_screen"),
    ) {
        val visualHeight = when {
            maxHeight < 620.dp -> 220.dp
            maxHeight < 720.dp -> 260.dp
            maxHeight < 850.dp -> 310.dp
            else -> 350.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp)
                .padding(top = 12.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StoryNavigation(
                pageCount = pages.size,
                selectedPageIndex = selectedPageIndex,
                isFinalPage = isFinalPage,
                onBack = { selectedPageIndex = (selectedPageIndex - 1).coerceAtLeast(0) },
                onSkip = onComplete,
            )

            Spacer(Modifier.height(12.dp))

            AnimatedContent(
                targetState = selectedPageIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(visualHeight)
                    .clipToBounds(),
                transitionSpec = { visualTransition() },
                contentKey = { it },
                label = "onboarding visual",
            ) { pageIndex ->
                PageVisual(
                    page = pages[pageIndex],
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(Modifier.height(28.dp))

            AnimatedContent(
                targetState = selectedPageIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .clipToBounds(),
                transitionSpec = { copyTransition() },
                contentKey = { it },
                label = "onboarding copy",
            ) { pageIndex ->
                CopyBlock(page = pages[pageIndex])
            }

            Spacer(Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .widthIn(min = 210.dp, max = 250.dp)
                    .height(58.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(100),
                        ambientColor = Peach.copy(alpha = 0.65f),
                        spotColor = Peach.copy(alpha = 0.65f),
                    )
                    .testTag(if (isFinalPage) "onboarding_get_started" else "onboarding_next"),
                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink,
                    contentColor = Color.White,
                ),
                onClick = if (isFinalPage) {
                    onComplete
                } else {
                    { selectedPageIndex = (selectedPageIndex + 1).coerceAtMost(lastPageIndex) }
                },
            ) {
                Text(
                    text = stringResource(if (isFinalPage) Res.string.onboarding_get_started else Res.string.onboarding_next),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    }
}

@Composable
private fun StoryNavigation(
    pageCount: Int,
    selectedPageIndex: Int,
    isFinalPage: Boolean,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (selectedPageIndex > 0) {
            TextButton(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
                    .testTag("onboarding_back"),
                onClick = onBack,
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = "<",
                    color = Ink,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            repeat(pageCount) { index ->
                val progress by animateFloatAsState(
                    targetValue = if (index <= selectedPageIndex) 1f else 0f,
                    animationSpec = tween(420, easing = FastOutSlowInEasing),
                    label = "story progress",
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(100))
                        .background(Track)
                        .testTag("onboarding_indicator_$index"),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(Ink),
                    )
                }
            }
        }

        TextButton(
            modifier = Modifier.testTag("onboarding_skip"),
            enabled = !isFinalPage,
            onClick = onSkip,
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            Text(
                text = stringResource(Res.string.onboarding_skip),
                color = if (isFinalPage) Color.Transparent else MutedInk,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun PageVisual(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    when (page.title) {
        OnboardingPageTitle.CaptureYourDay -> CaptureVisual(page, modifier)
        OnboardingPageTitle.UnderstandYourPatterns -> InsightVisual(page, modifier)
        OnboardingPageTitle.KeepEveryMemoryClose -> MemoriesVisual(page, modifier)
    }
}

@Composable
private fun CaptureVisual(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(Peach, SoftPeach, Canvas))),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(245.dp)
                .border(1.dp, Color.White.copy(alpha = 0.75f), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(190.dp)
                .border(1.dp, Color.White.copy(alpha = 0.75f), CircleShape),
        )

        PhotoBubble(
            image = page.images[0],
            size = 90.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 28.dp),
        )
        PhotoBubble(
            image = page.images[1],
            size = 62.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-24).dp, y = (-22).dp),
        )
        PhotoBubble(
            image = page.images[2],
            size = 58.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 28.dp, y = (-30).dp),
        )
        PhotoBubble(
            image = page.images[3],
            size = 68.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-34).dp, y = (-12).dp),
        )

        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(min = 212.dp),
            color = Color.White,
            shape = RoundedCornerShape(100),
            shadowElevation = 10.dp,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 17.dp),
                text = "Your day, remembered",
                color = Ink,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        MiniLabel(
            text = "MOOD",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 14.dp, y = 48.dp),
        )
        MiniLabel(
            text = "MEMORY",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp),
        )
    }
}

@Composable
private fun InsightVisual(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(SoftBlue),
    ) {
        ReflectionCard(
            title = "A calmer morning",
            subtitle = "Monday  ·  feeling focused",
            image = page.images[0],
            accent = Peach,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
                .fillMaxWidth(0.90f),
        )
        ReflectionCard(
            title = "A week of small wins",
            subtitle = "Your energy is trending up",
            image = page.images[1],
            accent = Sage,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.82f),
        )
        ReflectionCard(
            title = "Time outdoors",
            subtitle = "Your brightest moments",
            image = page.images[2],
            accent = Color(0xFFFFCFAD),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 34.dp)
                .fillMaxWidth(0.72f),
        )

        PhotoBubble(
            image = page.images[3],
            size = 54.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 18.dp, y = (-10).dp),
        )
        PhotoBubble(
            image = page.images[4],
            size = 48.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-20).dp, y = (-12).dp),
        )
    }
}

@Composable
private fun MemoriesVisual(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF5F5F4))
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MemoryStrip(
            topImage = page.images[0],
            bottomImage = page.images[1],
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(0.90f),
        )
        MemoryStrip(
            topImage = page.images[2],
            bottomImage = page.images[3],
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )
        MemoryStrip(
            topImage = page.images[4],
            bottomImage = page.images[5],
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(0.88f),
        )
    }
}

@Composable
private fun ReflectionCard(
    title: String,
    subtitle: String,
    image: DrawableResource,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 5.dp,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(100))
                    .background(accent),
            )
            Spacer(Modifier.width(10.dp))
            PhotoBubble(image = image, size = 38.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
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
private fun MemoryStrip(
    topImage: DrawableResource,
    bottomImage: DrawableResource,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(Color.White),
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            painter = painterResource(topImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.height(5.dp))
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            painter = painterResource(bottomImage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PhotoBubble(
    image: DrawableResource,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Image(
        modifier = modifier
            .size(size)
            .border(2.dp, Color.White, CircleShape)
            .clip(CircleShape),
        painter = painterResource(image),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun BoxScope.MiniLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Ink,
        shape = RoundedCornerShape(100),
        shadowElevation = 5.dp,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun CopyBlock(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.widthIn(max = 350.dp),
            text = page.headline,
            color = Ink,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(14.dp))
        Text(
            modifier = Modifier.widthIn(max = 330.dp),
            text = page.body,
            color = MutedInk,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun AnimatedContentTransitionScope<Int>.visualTransition() = if (targetState > initialState) {
    (
        slideInHorizontally(
            animationSpec = tween(520, easing = FastOutSlowInEasing),
            initialOffsetX = { it / 7 },
        ) + fadeIn(tween(360, delayMillis = 60))
        ).togetherWith(
        slideOutHorizontally(
            animationSpec = tween(420, easing = FastOutSlowInEasing),
            targetOffsetX = { -it / 12 },
        ) + fadeOut(tween(220)),
    )
} else {
    (
        slideInHorizontally(
            animationSpec = tween(520, easing = FastOutSlowInEasing),
            initialOffsetX = { -it / 7 },
        ) + fadeIn(tween(360, delayMillis = 60))
        ).togetherWith(
        slideOutHorizontally(
            animationSpec = tween(420, easing = FastOutSlowInEasing),
            targetOffsetX = { it / 12 },
        ) + fadeOut(tween(220)),
    )
}

private fun AnimatedContentTransitionScope<Int>.copyTransition() = (
    slideInVertically(
        animationSpec = tween(420, delayMillis = 80, easing = FastOutSlowInEasing),
        initialOffsetY = { it / 9 },
    ) + fadeIn(tween(300, delayMillis = 80))
    ).togetherWith(
    slideOutVertically(
        animationSpec = tween(240, easing = FastOutSlowInEasing),
        targetOffsetY = { -it / 14 },
    ) + fadeOut(tween(160)),
)

@Preview
@Composable
private fun OnboardingScreenContentPreview() {
    SuperDiaryPreviewTheme {
        OnboardingScreenContent(onComplete = {})
    }
}
