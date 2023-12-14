package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.WeeklySummary

class GetWeeklySummaryUseCase(private val dataSource: DataSource) {
    operator fun invoke(): WeeklySummary? = dataSource.getWeeklySummary()
}
