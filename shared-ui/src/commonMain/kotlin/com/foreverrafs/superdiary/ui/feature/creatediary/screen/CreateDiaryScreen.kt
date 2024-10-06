package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object CreateDiaryScreen : SuperDiaryScreen {

    @Composable
    fun Content(navController: NavController) {
        val createDiaryScreenModel: CreateDiaryViewModel = koinInject()

        val undoManager = rememberUndoableRichTextState()
        val richTextState = undoManager.richTextState
        val coroutineScope = rememberCoroutineScope()

        var isGeneratingFromAI by remember {
            mutableStateOf(false)
        }

        CreateDiaryScreenContent(
            onNavigateBack = navController::popBackStack,
            richTextState = richTextState,
            isGeneratingFromAi = isGeneratingFromAI,
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
            onSaveDiary = { entry ->
                createDiaryScreenModel.saveDiary(
                    Diary(
                        entry = entry,
                        date = Clock.System.now(),
                        isFavorite = false,
                    ),
                )

                navController.popBackStack()
            },
        )
    }
}
