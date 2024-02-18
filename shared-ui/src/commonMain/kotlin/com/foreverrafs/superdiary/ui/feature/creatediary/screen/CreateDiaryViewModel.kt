package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.core.logging.Logger
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CreateDiaryViewModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val diaryAI: DiaryAI,
    private val logger: Logger,
) : ScreenModel {

    fun saveDiary(diary: Diary) = screenModelScope.launch {
        addDiaryUseCase(diary)
        logger.i(Tag) {
            "Diary Saved!"
        }
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    companion object {
        private val Tag = CreateDiaryViewModel::class.simpleName.orEmpty()
    }
}
