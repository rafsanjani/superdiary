package com.foreverrafs.superdiary.data.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.withContext
import okio.IOException

class UpdateDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diary: Diary): Boolean = withContext(dispatchers.io) {
        try {
            dataSource.update(diary) != 0
        } catch (exception: IOException) {
            Logger.e(Tag, exception) {
                "Error Updating diary $diary"
            }
            false
        }
    }

    companion object {
        private val Tag = UpdateDiaryUseCase::class.simpleName.orEmpty()
    }
}
