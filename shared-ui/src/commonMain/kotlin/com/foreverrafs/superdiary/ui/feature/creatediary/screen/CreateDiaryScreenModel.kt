package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteDiaryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CreateDiaryScreenModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
    private val diaryAI: DiaryAI,
) : ScreenModel {

    fun saveDiary(diary: Diary) = screenModelScope.launch {
        addDiaryUseCase(diary)
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    fun deleteDiary(diary: Diary) = screenModelScope.launch {
        deleteDiaryUseCase(diary)
    }
}
