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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.creatediary.components.RichTextStyleRow
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.content_description_navigate_back
import superdiary.shared_ui.generated.resources.content_description_save_entry
import superdiary.shared_ui.generated.resources.label_diary_ai

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun CreateDiaryScreenContent(
    isGeneratingFromAi: Boolean,
    onGenerateAI: (prompt: String, wordCount: Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    richTextState: RichTextState = rememberRichTextState(),
    onSaveDiary: (entry: String) -> Unit,
) {
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
                            contentDescription = stringResource(Res.string.content_description_navigate_back),
                        )
                    }
                },
                tralingIcon = {
                    if (richTextState.annotatedString.isNotEmpty()) {
                        IconButton(
                            modifier = Modifier.testTag("button_save_diary"),
                            onClick = {
                                onSaveDiary(richTextState.toHtml())
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .clip(CircleShape),
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(Res.string.content_description_save_entry),
                            )
                        }
                    }
                },
            )
        },
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier.padding(it),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .imePadding(),
            ) {
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                RichTextStyleRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    state = richTextState,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.label_diary_ai),
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

                Spacer(modifier = Modifier.height(8.dp))

                // editable state
                OutlinedRichTextEditor(
                    state = richTextState,
                    modifier = Modifier
                        .testTag("diary_text_field")
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 25.sp,
                    ),
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
