package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.creatediary.components.RichTextStyleRow
import com.foreverrafs.superdiary.ui.format
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDiaryScreenContent(
    onNavigateBack: () -> Unit,
    onGenerateAI: (prompt: String, wordCount: Int) -> Unit,
    richTextState: RichTextState = rememberRichTextState(),
    diary: Diary?,
    isGeneratingFromAi: Boolean,
    onSaveDiary: (entry: String) -> Unit,
) {
    val readOnly = diary != null

    Scaffold(
        topBar = {
            SuperDiaryAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape),
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Navigate back",
                        )
                    }
                },
                saveIcon = {
                    if (readOnly) return@SuperDiaryAppBar
                    IconButton(
                        onClick = {
                            onSaveDiary(richTextState.toHtml())
                        },
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape),
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save entry",
                        )
                    }
                },
            )
        },
    ) {
        LaunchedEffect(Unit) {
            diary?.let {
                richTextState.setHtml(diary.entry)
            }
        }

        Surface(
            modifier = Modifier.padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
            ) {
                RichTextStyleRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = richTextState,
                )

                if (!readOnly) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(color = MaterialTheme.colorScheme.surface),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Diary AI:",
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        // Only enable AI suggestions when there is at least 50 characters entered
                        val enableSuggestionChip =
                            !isGeneratingFromAi && richTextState.annotatedString.text.length >= 50

                        DiaryAISuggestionChip(
                            words = 50,
                            enabled = enableSuggestionChip,
                            onClick = {
                                onGenerateAI(
                                    richTextState.annotatedString.text,
                                    50,
                                )
                            },
                        )

                        DiaryAISuggestionChip(
                            words = 100,
                            enabled = enableSuggestionChip,
                            onClick = {
                                onGenerateAI(
                                    richTextState.annotatedString.text,
                                    100,
                                )
                            },
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        AnimatedVisibility(visible = isGeneratingFromAi) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(color = MaterialTheme.colorScheme.surface),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = diary?.date?.toLocalDateTime(TimeZone.UTC)?.date?.format(
                                format = "EEE, MMM dd, yyyy",
                            ) ?: "",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedRichTextEditor(
                    state = richTextState,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 15.sp,
                    ),
                    readOnly = diary != null,
                )
            }
        }
    }
}

@Composable
private fun DiaryAISuggestionChip(words: Int, enabled: Boolean, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        label = {
            Text(
                text = "$words Words",
                style = MaterialTheme.typography.labelSmall,
            )
        },
        enabled = enabled,
    )
}
