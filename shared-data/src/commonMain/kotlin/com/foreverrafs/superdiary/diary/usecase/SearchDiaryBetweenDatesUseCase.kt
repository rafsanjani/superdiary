package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class SearchDiaryBetweenDatesUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(from: Instant, to: Instant): Flow<List<Diary>> {
        require(from <= to) {
            "The date $from should be less than or equal to $to"
        }
        return dataSource.find(from, to)
    }
}
