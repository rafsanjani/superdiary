package com.foreverrafs.superdiary.business.usecase.diarylist

import com.foreverrafs.superdiary.business.usecase.common.DeleteDiaryUseCase

//Set of usecases for fetching and interacting with diaries on any page that shows a list of diaries
data class DiaryListInteractor(
    val searchDiary: SearchDiaryUseCase,
    val fetchAllDiaries: GetAllDiariesUseCase,
    val deleteDiary: DeleteDiaryUseCase
)