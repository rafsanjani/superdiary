package com.foreverrafs.superdiary.list.domain.usecase

import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.list.domain.repository.DiaryListRepository

internal class GetDiaryByIdUseCase(
    private val repository: DiaryListRepository,
) {
    operator fun invoke(id: Long): Diary? = repository.getDiaryById(id)
}
