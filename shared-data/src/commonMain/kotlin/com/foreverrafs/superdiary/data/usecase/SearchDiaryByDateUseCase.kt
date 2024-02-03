package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class SearchDiaryByDateUseCase(
    private val dataSource: DataSource,
) {
    operator fun invoke(date: Instant): Flow<List<Diary>> {
        return dataSource.findByDate(date)
    }
}
