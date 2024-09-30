package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.WeeklySummary
import kotlinx.coroutines.withContext

class GetWeeklySummaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(): WeeklySummary? =
        withContext(dispatchers.io) { dataSource.getWeeklySummary() }
}
