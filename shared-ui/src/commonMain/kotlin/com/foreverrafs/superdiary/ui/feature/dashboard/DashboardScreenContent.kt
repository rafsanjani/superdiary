package com.foreverrafs.superdiary.ui.feature.dashboard

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.model.Streak
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.feature.details.DetailScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryItem
import com.foreverrafs.superdiary.ui.format
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.label_add_entry
import superdiary.shared_ui.generated.resources.label_button_retry
import superdiary.shared_ui.generated.resources.label_button_show_all
import superdiary.shared_ui.generated.resources.label_entries
import superdiary.shared_ui.generated.resources.label_glance_header_best_streak
import superdiary.shared_ui.generated.resources.label_glance_header_latest_entries
import superdiary.shared_ui.generated.resources.label_glance_header_streak
import superdiary.shared_ui.generated.resources.label_glance_header_weekly_summary
import superdiary.shared_ui.generated.resources.label_weekly_summary_error

@OptIn(ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun DashboardScreenContent(
    state: DashboardViewModel.DashboardScreenState,
    onAddEntry: () -> Unit,
    onToggleFavorite: (diary: Diary) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAll: () -> Unit,
) {
    if (state !is DashboardViewModel.DashboardScreenState.Content) return
    var isWeeklySummaryDisplayed by rememberSaveable { mutableStateOf(true) }
    val navigator = LocalScreenNavigator.current

    val dashboardItems = remember(state) {
        mutableStateListOf(
            DashboardSection(
                content = {
                    AtAGlance(
                        modifier = Modifier.animateItemPlacement().fillMaxWidth(),
                        state = state,
                    )
                },
                id = "atAGlance",
            ),
            DashboardSection(
                content = { onDismiss ->
                    WeeklySummaryCard(
                        modifier = Modifier.animateItemPlacement().fillMaxWidth().heightIn(max = 200.dp, min = 150.dp),
                        summary = state.weeklySummary,
                        onDismiss = onDismiss,
                    )
                },
                id = "weeklySummary",
            ),
            DashboardSection(
                content = {
                    val itemCount = if (isWeeklySummaryDisplayed) 2 else 4

                    if (state.latestEntries.isNotEmpty()) {
                        LatestEntries(
                            modifier = Modifier.animateItemPlacement(),
                            diaries = state.latestEntries.take(itemCount),
                            onSeeAll = onSeeAll,
                            onDiaryClicked = {
                                navigator.push(DetailScreen(it))
                            },
                            onToggleFavorite = onToggleFavorite,
                        )
                    } else {
                        Button(
                            onClick = onAddEntry,
                            modifier = Modifier.testTag("button_add_entry"),
                        ) {
                            Text(
                                text = stringResource(Res.string.label_add_entry),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                },
                id = "latestEntries",
            ),
        )
    }

    LazyColumn(
        modifier = modifier
            .testTag("dashboard_content_list")
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items = dashboardItems, key = { it.id }) { content ->
            content.content(this) {
                isWeeklySummaryDisplayed = false
                dashboardItems.remove(dashboardItems.firstOrNull { it.id == content.id })
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun AtAGlance(
    state: DashboardViewModel.DashboardScreenState.Content,
    modifier: Modifier = Modifier,
) {
    val animationState = remember { MutableTransitionState(false) }

    val currentStreakCount by animateIntAsState(
        targetValue = if (animationState.targetState) state.currentStreak.count else 0,
        animationSpec = tween(durationMillis = 1000),
    )

    val bestStreakCount by animateIntAsState(
        targetValue = if (animationState.targetState) state.bestStreak.count else 0,
        animationSpec = tween(durationMillis = 1000),
    )

    val totalEntries by animateIntAsState(
        targetValue = if (animationState.targetState) state.totalEntries.toInt() else 0,
        animationSpec = tween(durationMillis = 1000),
    )

    LaunchedEffect(Unit) {
        animationState.targetState = true
    }

    Column(modifier = modifier) {
        Text(
            text = "At a glance...",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val dashboardCardModifier = Modifier.weight(1f).aspectRatio(1f)

            fun streakCaption(streak: Streak): String {
                val dateFormatPattern = "MMM dd"
                return if (streak.count != 0) {
                    "${streak.startDate.format(dateFormatPattern)} - ${streak.endDate.format(dateFormatPattern)}"
                } else {
                    "-"
                }
            }

            GlanceCard(
                modifier = dashboardCardModifier,
                title = stringResource(Res.string.label_entries),
                content = totalEntries.toString(),
            )

            GlanceCard(
                modifier = dashboardCardModifier,
                title = stringResource(Res.string.label_glance_header_streak),
                // Because formatted string resources doesn't cause recomposition
                content = "$currentStreakCount days",
                caption = streakCaption(streak = state.currentStreak),
            )

            GlanceCard(
                modifier = dashboardCardModifier,
                title = stringResource(Res.string.label_glance_header_best_streak),
                // Because formatted string resources doesn't cause recomposition
                content = "$bestStreakCount days",
                caption = streakCaption(state.bestStreak),
            )
        }
    }
}

@Composable
fun GlanceCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
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

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun LatestEntries(
    diaries: List<Diary>,
    onSeeAll: () -> Unit,
    onToggleFavorite: (diary: Diary) -> Unit,
    modifier: Modifier = Modifier,
    onDiaryClicked: (diary: Diary) -> Unit,
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
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.label_glance_header_latest_entries),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(stringResource(Res.string.label_button_show_all), style = MaterialTheme.typography.labelSmall)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            diaries.forEachIndexed { index, diary ->
                DiaryItem(
                    diary = diary,
                    selected = false,
                    inSelectionMode = false,
                    modifier = Modifier
                        .clickable(onClick = { onDiaryClicked(diary) })
                        .testTag("diary_list_item_$index"),
                    onToggleFavorite = { onToggleFavorite(diary) },
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun WeeklySummaryCard(
    summary: String?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.label_glance_header_weekly_summary),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.headlineMedium,
                )

                Icon(
                    modifier = Modifier.clickable(onClick = onDismiss),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }

            Text(
                modifier = Modifier.padding(4.dp),
                text = summary ?: stringResource(Res.string.label_weekly_summary_error),
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
                    Text(stringResource(Res.string.label_button_retry))
                }
            }
        }
    }
}

data class DashboardSection(
    val content: @Composable LazyItemScope.(onDismiss: () -> Unit) -> Unit,
    val id: String,
)
