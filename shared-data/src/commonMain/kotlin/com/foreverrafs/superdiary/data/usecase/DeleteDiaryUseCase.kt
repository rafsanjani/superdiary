package com.foreverrafs.superdiary.data.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.core.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.model.Diary
import kotlinx.coroutines.withContext
import okio.IOException

class DeleteDiaryUseCase(
    private val dataSource: DataSource,
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diaries: List<Diary>): Int = withContext(dispatchers.io) {
        try {
            Logger.i(Tag) {
                "Deleted ${diaries.count()} entries"
            }
            dataSource.delete(diaries)
        } catch (exception: IOException) {
            Logger.e(Tag, exception) {
                "Error deleting diary or diaries"
            }

            0
        }
    }

    companion object {
        private val Tag = DeleteDiaryUseCase::class.simpleName.orEmpty()
    }
}
