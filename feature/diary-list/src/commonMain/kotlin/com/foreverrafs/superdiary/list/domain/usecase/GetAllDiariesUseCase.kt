package com.foreverrafs.superdiary.list.domain.usecase

import androidx.paging.PagingData
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAllDiariesUseCase(
    private val repository: DiaryListRepository,
) {
    operator fun invoke(): Flow<PagingData<Diary>> = repository.getAllDiaries()
}
