package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

class DeleteMultipleDiariesUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(diaries: List<Diary>): Int {
        return try {
            dataSource.delete(diaries)
        } catch (exception: Exception) {
            0
        }
    }
}
