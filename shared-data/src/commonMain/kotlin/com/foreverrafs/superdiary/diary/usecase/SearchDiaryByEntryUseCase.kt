package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary

class SearchDiaryByEntryUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(entry: String): List<Diary> {
        return dataSource.find(entry)
    }
}
