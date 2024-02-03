package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.WeeklySummary

class GetWeeklySummaryUseCase(private val dataSource: DataSource) {
    operator fun invoke(): WeeklySummary? = dataSource.getWeeklySummary()
}
