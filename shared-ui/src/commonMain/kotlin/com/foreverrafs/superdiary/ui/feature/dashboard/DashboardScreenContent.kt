package com.foreverrafs.superdiary.ui.feature.dashboard

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Composable
fun DashboardScreenContent(
    state: DashboardScreenModel.DashboardScreenState,
    onAddEntry: () -> Unit,
    onSeeAll: () -> Unit,
) {
    if (state !is DashboardScreenModel.DashboardScreenState.Content) return

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.offset(y = 30.dp),
                onClick = onAddEntry,
            ) {
                Icon(Icons.Default.Add, null)
            }
        },
    ) {
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
                targetValue = if (animationState.targetState) 20 else 0,
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
                SectionCard(
                    modifier = dashboardCardModifier,
                    title = {
                        SectionCardTitle("Entries")
                    },
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = totalEntries.toString(),
                        style = MaterialTheme.typography.displayLarge,
                    )
                }

                SectionCard(
                    modifier = dashboardCardModifier,
                    title = {
                        SectionCardTitle(text = "Streak 🔥")
                    },
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "$streaks days",
                        style = MaterialTheme.typography.displayLarge,
                    )

                    Text(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = "Jun 20 - Jul 20",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(itemSpacing))

            SectionCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                title = {
                },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Text(
                        text = "Weekly Summary",
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Text(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(start = 4.dp, end = 4.dp, top = 40.dp, bottom = 4.dp),
                        text = state.weeklySummary,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Justify,
                        lineHeight = 28.sp,
                    )
                }
            }

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
                ) { onSeeAll() }
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Latest Entry", style = MaterialTheme.typography.headlineMedium)

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
                    modifier = Modifier.clickable { onSeeAll() },
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
private fun SectionCardTitle(
    text: String,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        textAlign = textAlign,
        style = MaterialTheme.typography.headlineMedium,
    )
}

@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    title: @Composable BoxScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                title()
            }
            content()
        }
    }
}
