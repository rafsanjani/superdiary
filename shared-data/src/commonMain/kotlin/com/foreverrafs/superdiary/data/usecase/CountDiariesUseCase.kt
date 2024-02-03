package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.datasource.DataSource

class CountDiariesUseCase(private val dataSource: DataSource) {
    operator fun invoke(): Long = dataSource.countEntries()
}
