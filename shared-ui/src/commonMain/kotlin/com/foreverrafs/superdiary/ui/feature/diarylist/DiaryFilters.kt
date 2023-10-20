package com.foreverrafs.superdiary.ui.feature.diarylist

import kotlinx.datetime.LocalDate

data class DiaryFilters(
    val entry: String = "",
    val date: LocalDate? = null,
    val sort: DiarySortCriteria? = null,
)
