package com.components.diarylist

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import kotlinx.datetime.LocalDate

data class DiaryFilters(
    val entry: String = "",
    val date: LocalDate? = null,
    val sort: DiarySortCriteria? = null,
) {
    companion object {
        val Saver: Saver<DiaryFilters, *> = mapSaver(
            save = { diaryFilters ->
                mapOf(
                    DIARY_FILTER_ENTRY to diaryFilters.entry,
                    DIARY_FILTER_DATE to diaryFilters.date.toString(),
                    DIARY_FILTER_SORT to diaryFilters.sort?.name,
                )
            },
            restore = { savedState ->
                val entry = savedState[DIARY_FILTER_ENTRY] as String?
                val date = savedState[DIARY_FILTER_DATE] as String?
                val sort = savedState[DIARY_FILTER_SORT] as String?

                DiaryFilters(
                    entry = entry.orEmpty(),
                    date = if (date != null && date != "null") {
                        LocalDate.parse(date)
                    } else {
                        null
                    },
                    sort = sort?.let { DiarySortCriteria.valueOf(it) },
                )
            },
        )

        private const val DIARY_FILTER_ENTRY = "entry"
        private const val DIARY_FILTER_DATE = "date"
        private const val DIARY_FILTER_SORT = "sort"
    }
}
