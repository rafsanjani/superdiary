package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Instant

class SearchDiaryBetweenDatesUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(from: Instant, to: Instant): Flow<List<Diary>> {
        require(from <= to) {
            "The date $from should be less than or equal to $to"
        }
        return dataSource.find(from, to).flowOn(dispatchers.io)
    }
}
