package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.LocalDate

class SearchDiaryBetweenDatesUseCase(
    private val dataSource: DataSource,
) {
    suspend operator fun invoke(from: LocalDate, to: LocalDate): List<Diary> {
        require(from <= to) {
            "The date $from should be less than or equal to $to"
        }
        return dataSource.find(from.toString(), to.toString())
    }
}
