package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource

class CountDiariesUseCase(private val dataSource: DataSource) {
    operator fun invoke(): Long = dataSource.countEntries()
}
