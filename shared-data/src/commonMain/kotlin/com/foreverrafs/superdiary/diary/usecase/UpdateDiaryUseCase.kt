package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

class UpdateDiaryUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(diary: Diary): Boolean {
        return try {
            dataSource.update(diary) != 0
        } catch (exception: Exception) {
            false
        }
    }
}
