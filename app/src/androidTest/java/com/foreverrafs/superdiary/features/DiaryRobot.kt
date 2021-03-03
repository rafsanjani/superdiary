package com.foreverrafs.superdiary.features

import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.util.BaseRobot
import java.time.LocalDate
import java.time.Month
import java.time.MonthDay
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

fun diaryRobot(func: DiaryRobot.() -> Unit) = DiaryRobot().apply { func() }

class DiaryRobot : BaseRobot() {
    fun scrollToNextMonth() {
        swipeLeft(R.id.diaryCalendarView)
    }

    fun scrollToPreviousMonth() {
        swipeRight(R.id.diaryCalendarView)
    }

    fun scrollToDate(date: MonthDay) {
        scrollToMonth(date.month)
        selectDay(date.dayOfMonth.toString())
    }

    fun scrollToMonth(month: Month, pivotToday: Boolean = false) {
        if (pivotToday)
            scrollToToday()

        // TODO: 24/02/21  
    }

    fun verifyAddButtonIsDisplayed() {
        verifyViewWithIdIsDisplayed(R.id.btnNewEntry)
    }

    fun verifyMonthSelected(month: Month) {
        verifyViewContainsTextIsDisplayed(
            month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )
        )

    }

    fun verifyDiaryDisplayed(message: String) {
        verifyListItemWithText(text = message, viewHolderContainerRootViewId = R.id.diaryContainer)
    }

    fun scrollToToday() {
        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val todayDate = formatter.format(LocalDate.now())

        tapViewWithId(viewId = R.id.titleText)

        verifyViewWithTextIsDisplayed(todayDate)
    }

    fun selectDay(day: String) {
        tapItemInList(R.id.dayViewContainer, day)
    }

    fun addDiary(message: String) {
        tapViewWithId(R.id.btnNewEntry)
        typeText(R.id.textDiaryEntry, message)
        tapViewWithId(R.id.btnDone)
    }
}