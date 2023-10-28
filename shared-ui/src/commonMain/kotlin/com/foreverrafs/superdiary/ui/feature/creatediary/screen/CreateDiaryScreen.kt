package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.foreverrafs.superdiary.diary.generator.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

object CreateDiaryScreen : Screen {

    @Composable
    override fun Content() {
        val createDiaryScreenModel: CreateDiaryScreenModel = getScreenModel()
        val navigator = LocalScreenNavigator.current
        val richTextState = rememberRichTextState()
        val coroutineScope = rememberCoroutineScope()

        CreateDiaryScreenContent(
            onNavigateBack = navigator::pop,
            richTextState = richTextState,
            onGenerateAI = { prompt, wordCount ->
                var generatedWords = ""

                coroutineScope.launch {
                    createDiaryScreenModel.generateAIDiary(
                        prompt = prompt,
                        wordCount = wordCount,
                    ).collect { chunk ->
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
                    isFavorite = false,
                ),
            )

            navigator.pop()
        }
    }
}

class CreateDiaryScreenModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val diaryAI: DiaryAI,
) : ScreenModel {

    fun saveDiary(diary: Diary) = screenModelScope.launch {
        addDiaryUseCase(diary)
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)
}
