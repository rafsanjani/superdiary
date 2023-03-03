package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import kotlinx.datetime.LocalDate

class SearchDiaryUseCase(
    private val dataSource: DataSource,
) {
    suspend fun searchByEntry(entry: String): List<Diary> {
        return dataSource.find(entry)
    }

    suspend fun searchByDate(date: LocalDate): List<Diary> {
        return dataSource.findByDate(date.toString())
    }

    suspend fun searchBetween(from: LocalDate, to: LocalDate): List<Diary> {
        require(from <= to) {
            "The date $from should be less than or equal to $to"
        }
        return dataSource.find(from.toString(), to.toString())
    }
}
