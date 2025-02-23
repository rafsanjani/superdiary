package com.foreverrafs.superdiary.list.domain.usecase

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetAllDiariesUseCase(
    private val repository: DiaryListRepository,
) {
    operator fun invoke(): Flow<DiaryListResult> = repository.getAllDiaries().map { Result.Success(it) }
}
