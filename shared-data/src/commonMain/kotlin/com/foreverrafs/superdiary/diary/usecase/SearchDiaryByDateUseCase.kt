package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class SearchDiaryByDateUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(date: Instant): Flow<List<Diary>> {
        return dataSource.findByDate(date)
    }
}
