package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CreateDiaryScreen(val diary: Diary? = null) : Screen {

    @Composable
    override fun Content() {
        val createDiaryScreenModel: CreateDiaryScreenModel = getScreenModel()
        val navigator = LocalScreenNavigator.current

        val undoManager = rememberUndoableRichTextState()
        val richTextState = undoManager.richTextState
        val coroutineScope = rememberCoroutineScope()

        var isGeneratingFromAI by remember {
            mutableStateOf(false)
        }

        var showDeleteDialog by remember {
            mutableStateOf(false)
        }

        if (showDeleteDialog) {
            ConfirmDeleteDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    if (diary != null) {
                        createDiaryScreenModel.deleteDiary(diary)
                    }
                    showDeleteDialog = false
                },
            )
        }

        CreateDiaryScreenContent(
            onNavigateBack = navigator::pop,
            richTextState = richTextState,
            diary = diary,
            isGeneratingFromAi = isGeneratingFromAI,
            onDeleteDiary = {
                showDeleteDialog = true
            },
            onGenerateAI = { prompt, wordCount ->
                undoManager.save()
                var generatedWords = ""

                coroutineScope.launch {
                    createDiaryScreenModel
                        .generateAIDiary(
                            prompt = prompt,
                            wordCount = wordCount,
                        )
                        .onStart {
                            isGeneratingFromAI = true
                            richTextState.setHtml("<p>Generating diary...</p>")
                        }
                        .catch {
                            isGeneratingFromAI = false
                            richTextState.setHtml("<p style=\"color:red\">Error generating entry</p>")
                            println(it)
                        }
                        .onCompletion {
                            isGeneratingFromAI = false
                        }
                        .collect { chunk ->
                            generatedWords += chunk
                            richTextState.setHtml("<p>$generatedWords</p>")
                        }
                }
            },
        ) { entry ->
            createDiaryScreenModel.saveDiary(
                Diary(
                    entry = entry,
                    date = Clock.System
                        .now(),
                    isFavorite = diary?.isFavorite ?: false,
                    id = diary?.id,
                ),
            )

            navigator.pop()
        }
    }
}
