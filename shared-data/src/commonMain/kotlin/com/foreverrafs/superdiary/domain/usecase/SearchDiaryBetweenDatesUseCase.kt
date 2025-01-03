package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
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
