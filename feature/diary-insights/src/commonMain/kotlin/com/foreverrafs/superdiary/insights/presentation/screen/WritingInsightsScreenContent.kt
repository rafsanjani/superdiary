package com.foreverrafs.superdiary.insights.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.insights.domain.model.WritingInsightTheme
import com.foreverrafs.superdiary.insights.domain.model.WritingInsightThemeType
import com.foreverrafs.superdiary.insights.domain.model.WritingStats
import com.foreverrafs.superdiary.insights.presentation.WritingInsightsViewState

@Composable
fun WritingInsightsScreenContent(
    screenState: WritingInsightsViewState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onDismissError: () -> Unit = {},
) {
    val currentOnDismissError by rememberUpdatedState(onDismissError)
    val errorText = (screenState as? WritingInsightsViewState.Content)?.errorText

    LaunchedEffect(errorText) {
        errorText?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long,
            )
            currentOnDismissError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(
                avatarUrl = avatarUrl,
                onProfileClick = onProfileClick,
                title = "Writing insights",
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            when (screenState) {
                WritingInsightsViewState.Loading -> LoadingContent()

                WritingInsightsViewState.Empty -> EmptyContent()

                is WritingInsightsViewState.Error -> ErrorContent(
                    message = screenState.message,
                    onRetry = onRefresh,
                )

                is WritingInsightsViewState.Content -> InsightsContent(
                    state = screenState,
                    onRefresh = onRefresh,
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator()
        Text(
            text = "Looking for patterns in your writing…",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Your insights will grow with your diary",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = "Write your first entry and come back here to see patterns in your writing habits.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onRetry) {
            Text("Try again")
        }
    }
}

@Composable
private fun InsightsContent(
    state: WritingInsightsViewState.Content,
    onRefresh: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("writing_insights"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            StatsSection(stats = state.stats)
        }

        item {
            InsightThemesSection(
                insights = state.insights,
                isGenerating = state.isGenerating,
                onRefresh = onRefresh,
            )
        }

        item {
            Text(
                text = "AI insights send your entry text to the configured AI provider and may miss context. Use them as writing prompts, not professional advice.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun StatsSection(stats: WritingStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Entries",
                value = stats.entriesAnalyzed.toString(),
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Total words",
                value = stats.totalWords.toString(),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                label = "Average length",
                value = "${stats.averageWordsPerEntry} words",
                modifier = Modifier.weight(1f),
            )
            StatCard(
                label = "Longest entry",
                value = "${stats.longestEntryWords} words",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun InsightThemesSection(
    insights: List<WritingInsightTheme>,
    isGenerating: Boolean,
    onRefresh: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "AI writing coach",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        insights.forEach { insight ->
            InsightThemeCard(insight)
        }

        if (isGenerating) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                    )
                    Text(
                        text = if (insights.isEmpty()) {
                            "Reading all your entries…"
                        } else {
                            "Refreshing your insights…"
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        } else {
            OutlinedButton(onClick = onRefresh) {
                Text("Refresh insights")
            }
        }
    }
}

@Composable
private fun InsightThemeCard(insight: WritingInsightTheme) {
    val colorScheme = MaterialTheme.colorScheme
    val (containerColor, contentColor) = when (insight.type) {
        WritingInsightThemeType.Patterns ->
            colorScheme.primaryContainer to colorScheme.onPrimaryContainer

        WritingInsightThemeType.Consistency ->
            colorScheme.secondary to colorScheme.onSecondary

        WritingInsightThemeType.TryNext ->
            colorScheme.tertiaryContainer to colorScheme.onTertiaryContainer
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("insight_${insight.type.name}"),
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(22.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = insight.heading,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = insight.content,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WritingInsightsPreview() {
    SuperDiaryPreviewTheme {
        WritingInsightsScreenContent(
            screenState = WritingInsightsViewState.Content(
                stats = WritingStats(
                    entriesAnalyzed = 24,
                    totalWords = 3_240,
                    averageWordsPerEntry = 135,
                    longestEntryWords = 412,
                ),
                insights = listOf(
                    WritingInsightTheme(
                        type = WritingInsightThemeType.Patterns,
                        content = "Your entries often begin with a quick recap before moving into more reflective detail.",
                    ),
                    WritingInsightTheme(
                        type = WritingInsightThemeType.Consistency,
                        content = "Your longer entries appear toward the end of the week.",
                    ),
                    WritingInsightTheme(
                        type = WritingInsightThemeType.TryNext,
                        content = "Add one sensory detail to your next entry.",
                    ),
                ),
            ),
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@PreviewLightDark
@Composable
private fun WritingInsightsPreviewLoading() {
    SuperDiaryPreviewTheme {
        WritingInsightsScreenContent(
            screenState = WritingInsightsViewState.Loading,
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@PreviewLightDark
@Composable
private fun WritingInsightsPreviewError() {
    SuperDiaryPreviewTheme {
        WritingInsightsScreenContent(
            screenState = WritingInsightsViewState.Error(
                message = "Error loading insights",
            ),
            snackbarHostState = SnackbarHostState(),
        )
    }
}
