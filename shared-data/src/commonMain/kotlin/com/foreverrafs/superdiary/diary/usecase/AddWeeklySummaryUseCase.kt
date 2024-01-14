package com.foreverrafs.superdiary.diary.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.WeeklySummary

class AddWeeklySummaryUseCase(private val dataSource: DataSource) {
    operator fun invoke(weeklySummary: WeeklySummary) {
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
