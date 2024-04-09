package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.Paparazzi
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.feature.details.DetailScreenContent
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DiaryDetailSnapshotTests(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {
    private val testClock = object : Clock {
        // 2023-11-10
        override fun now(): Instant = Instant.parse("2023-11-10T00:00:00.850951Z")
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        maxPercentDifference = 0.1,
    )

    @Test
    fun `Diary detail screen`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                DetailScreenContent(
                    onDeleteDiary = {},
                    onNavigateBack = {},
                    diary = Diary(
                        entry = """
                        Hello Diary, I did something awful today too.
                        I kept eating a very large bowl of rice till I couldn't take
                        it any much longer. I think this will go down in history as
                        the greatest rice eating bout of all time.
                        """.trimIndent(),
                        id = 1000,
                        date = testClock.now(),
                        isFavorite = false,
                    ),
                )
            }
        }
    }
}
