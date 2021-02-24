package com.foreverrafs.superdiary.features.diary_list

import com.foreverrafs.superdiary.BaseTest
import com.foreverrafs.superdiary.data.mockDiaries
import com.foreverrafs.superdiary.di.PersistenceModule
import com.foreverrafs.superdiary.features.diaryRobot
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import java.time.LocalDate

@UninstallModules(PersistenceModule::class)
@HiltAndroidTest
class DiaryListTest : BaseTest() {

    @Test
    fun testDiaryListDisplayed() {
        diaryRobot {
            val today = LocalDate.now()

            selectDay(today.dayOfMonth.toString())
            verifyDiaryDisplayed(mockDiaries[4].message)
            verifyDiaryDisplayed(mockDiaries[5].message)
            verifyDiaryDisplayed(mockDiaries[6].message)

            selectDay(today.minusDays(1).dayOfMonth.toString())
            verifyDiaryDisplayed(mockDiaries[2].message)
            verifyDiaryDisplayed(mockDiaries[3].message)

            selectDay(today.minusDays(2).dayOfMonth.toString())
            verifyDiaryDisplayed(mockDiaries[0].message)
            verifyDiaryDisplayed(mockDiaries[1].message)
        }
    }
}