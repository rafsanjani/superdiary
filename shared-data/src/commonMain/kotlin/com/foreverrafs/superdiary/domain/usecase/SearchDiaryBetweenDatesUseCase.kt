package com.foreverrafs.superdiary.domain.usecase

import androidx.paging.PagingData
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class SearchDiaryBetweenDatesUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(from: kotlin.time.Instant, to: kotlin.time.Instant): Flow<PagingData<Diary>> {
        require(from <= to) {
            "The date $from should be less than or equal to $to"
        }
        return dataSource.findPaged(from, to).flowOn(dispatchers.io)
    }
}
