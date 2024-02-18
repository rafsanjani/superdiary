package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import kotlinx.coroutines.withContext

class CountDiariesUseCase(private val dataSource: DataSource, private val dispatchers: AppCoroutineDispatchers) {
    suspend operator fun invoke(): Long = withContext(dispatchers.io) { dataSource.countEntries() }
}
