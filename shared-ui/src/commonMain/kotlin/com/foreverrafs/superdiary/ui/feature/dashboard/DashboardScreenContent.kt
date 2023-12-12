package com.foreverrafs.superdiary.ui.feature.dashboard

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryItem
import com.foreverrafs.superdiary.ui.format

@Composable
fun DashboardScreenContent(
    state: DashboardScreenModel.DashboardScreenState,
    onSeeAll: () -> Unit,
) {
    if (state !is DashboardScreenModel.DashboardScreenState.Content) return

    Scaffold {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            val dashboardCardModifier = Modifier
                .weight(1f)
                .aspectRatio(1f)

            val itemSpacing = 8.dp

            val animationState = remember { MutableTransitionState(false) }

            val streaks by animateIntAsState(
                targetValue = if (animationState.targetState) state.streak.count else 0,
                animationSpec = tween(durationMillis = 1000),
            )

            val totalEntries by animateIntAsState(
                targetValue = if (animationState.targetState) state.totalEntries.toInt() else 0,
                animationSpec = tween(durationMillis = 1000),
            )

            LaunchedEffect(Unit) {
                animationState.targetState = true
            }

            Text(
                text = "At a glance...",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            ) {
                GlanceCard(
                    modifier = dashboardCardModifier,
                    title = "Entries",
                    content = totalEntries.toString(),
                )

                GlanceCard(
                    modifier = dashboardCardModifier,
                    title = "Streak 🔥",
                    content = "$streaks days",
                    caption = state.streak.dates.joinToString(" - ") { it?.format("MMM dd") ?: "" },
                )

                GlanceCard(
                    modifier = dashboardCardModifier,
                    title = "Best Streak",
                    content = "$streaks days",
                    caption = "Jun 20 - Jul 20",
                )
            }

            Spacer(modifier = Modifier.height(itemSpacing))

            WeeklySummaryCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp, min = 150.dp),
                summary = state.weeklySummary,
            )

            Spacer(
                modifier = Modifier
                    .height(4.dp),
            )

            if (state.latestEntries.isNotEmpty()) {
                LatestEntries(diaries = state.latestEntries, onSeeAll = onSeeAll)
            }
        }
    }
}

@Composable
fun GlanceCard(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    caption: String = "",
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = content,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )

            Text(
                text = caption,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
private fun LatestEntries(
    modifier: Modifier = Modifier,
    onSeeAll: () -> Unit,
    diaries: List<Diary>,
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource(),
                    onClick = onSeeAll,
                )
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Latest Entries", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(diaries) {
                DiaryItem(
                    modifier = Modifier.clickable(onClick = onSeeAll),
                    inSelectionMode = false,
                    onToggleFavorite = {},
                    selected = false,
                    diary = it,
                )
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(
    modifier: Modifier = Modifier,
    summary: String?,
) {
    Card(
        modifier = modifier.verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Weekly Summary",
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = summary ?: "Error generating weekly summary",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify,
                lineHeight = 28.sp,
            )

            if (summary == null) {
                TextButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp),
                    onClick = {},
                ) {
                    Text("Retry")
                }
            }
        }
    }
}
