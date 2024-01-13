package com.foreverrafs.superdiary.diary.usecase

import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import okio.IOException

class DeleteDiaryUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(diaries: List<Diary>): Int {
        return try {
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
