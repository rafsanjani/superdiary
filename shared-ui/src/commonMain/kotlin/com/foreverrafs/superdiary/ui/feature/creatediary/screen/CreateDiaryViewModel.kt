package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CreateDiaryViewModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val diaryAI: DiaryAI,
    private val logger: AggregateLogger,
) : ViewModel() {

    fun saveDiary(diary: Diary) = viewModelScope.launch {
        addDiaryUseCase(diary)
        logger.i(Tag) {
            "Diary entry successfully saved: $diary"
        }
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    companion object {
        private val Tag = CreateDiaryViewModel::class.simpleName.orEmpty()
    }
}
