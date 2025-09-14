package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.withContext

class AddWeeklySummaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(weeklySummary: WeeklySummary) = withContext(dispatchers.io) {
        dataSource.save(
            weeklySummary,
        )
    }
}
