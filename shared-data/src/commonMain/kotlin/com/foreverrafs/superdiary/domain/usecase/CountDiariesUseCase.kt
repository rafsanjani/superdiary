package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.repository.DataSource
import kotlinx.coroutines.withContext

class CountDiariesUseCase(private val dataSource: DataSource, private val dispatchers: AppCoroutineDispatchers) {
    suspend operator fun invoke(): Long = withContext(dispatchers.io) { dataSource.countEntries() }
}
