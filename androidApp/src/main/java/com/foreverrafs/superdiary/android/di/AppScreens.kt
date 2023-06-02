package com.foreverrafs.superdiary.android.di

import com.foreverrafs.superdiary.android.screens.CalendarScreen
import com.foreverrafs.superdiary.android.screens.CreateDiaryScreen
import com.foreverrafs.superdiary.android.screens.DiaryTimelineScreen
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreens(
    val diaryTimelineScreen: DiaryTimelineScreen,
    val createDiaryScreen: CreateDiaryScreen,
    val calendarScreen: CalendarScreen
)
