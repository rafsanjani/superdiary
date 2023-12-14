package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.WeeklySummary

class AddWeeklySummaryUseCase(private val dataSource: DataSource) {
    operator fun invoke(weeklySummary: WeeklySummary) {
        dataSource.insertWeeklySummary(
            weeklySummary,
        )
    }
}
