package com.foreverrafs.superdiary.domain.usecase

import androidx.paging.PagingData
import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class SearchDiaryByDateUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    operator fun invoke(date: kotlin.time.Instant): Flow<PagingData<Diary>> = dataSource.findByDatePaged(date).flowOn(dispatchers.io)

    operator fun invoke(entry: String, date: kotlin.time.Instant): Flow<PagingData<Diary>> {
        val timeZone = TimeZone.currentSystemDefault()
        val currentDate = date.toLocalDateTime(timeZone).date
        val startOfDay = currentDate.atStartOfDayIn(timeZone)
        val endOfDay = LocalDateTime(
            year = currentDate.year,
            month = currentDate.month.number,
            day = currentDate.day,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999,
        ).toInstant(timeZone)

        return dataSource.findPaged(
            entry = entry,
            from = startOfDay,
            to = endOfDay,
        ).flowOn(dispatchers.io)
    }
}
