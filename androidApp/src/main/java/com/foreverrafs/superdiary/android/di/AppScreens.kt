package com.foreverrafs.superdiary.android.di

import com.foreverrafs.superdiary.android.screens.DiaryTimelineScreen
import me.tatarka.inject.annotations.Inject

@Inject
class AppScreens(
    val diaryTimelineScreen: DiaryTimelineScreen
)
