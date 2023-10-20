package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.LocalDate

class SearchDiaryByDateUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(date: LocalDate): List<Diary> {
        return dataSource.findByDate(date.toString())
    }
}
