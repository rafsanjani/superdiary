package com.foreverrafs.superdiary.data.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.WeeklySummary
import kotlinx.coroutines.withContext

class AddWeeklySummaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(weeklySummary: WeeklySummary) = withContext(dispatchers.io) {
        dataSource.insertWeeklySummary(
            weeklySummary,
        )
        Logger.i(Tag) {
            "Weekly summary saved!"
        }
    }

    companion object {
        private val Tag = AddWeeklySummaryUseCase::class.simpleName.orEmpty()
    }
}
