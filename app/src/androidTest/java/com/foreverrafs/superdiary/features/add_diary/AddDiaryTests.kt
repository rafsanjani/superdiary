package com.foreverrafs.superdiary.features.add_diary

import com.foreverrafs.superdiary.BaseTest
import com.foreverrafs.superdiary.di.PersistenceModule
import com.foreverrafs.superdiary.features.diaryRobot
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import java.time.Month
import java.time.MonthDay

@UninstallModules(PersistenceModule::class)
@HiltAndroidTest
class AddDiaryTests : BaseTest() {
    @Test
    fun testAddDiary() {
        diaryRobot {
            scrollToDate(MonthDay.of(Month.AUGUST, 12))
            scrollToToday()
            verifyAddButtonIsDisplayed()
        }
    }
}