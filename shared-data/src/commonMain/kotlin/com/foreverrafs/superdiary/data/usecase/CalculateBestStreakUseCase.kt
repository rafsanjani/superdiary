package com.foreverrafs.superdiary.data.usecase

import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.model.Streak
import com.foreverrafs.superdiary.data.utils.toDate
import kotlinx.datetime.minus

class CalculateBestStreakUseCase {
    operator fun invoke(diaries: List<Diary>): Streak {
        var streak = Streak(
            count = 0,
            startDate = diaries.first().date.toDate(),
            endDate = diaries.first().date.toDate()
        )

        var bestStreak = streak

        diaries
            .asSequence()
            .sortedBy { it.date }
            .forEach {
                streak = if ((it.date.toDate() - streak.endDate).days == 1) {
                    val currentStreak = streak.copy(
                        count = streak.count + 1,
                        endDate = it.date.toDate()
                    )

                    if (currentStreak.count > bestStreak.count) bestStreak = currentStreak
                    currentStreak
                } else {
                    Streak(0, it.date.toDate(), it.date.toDate())
                }
            }

        return bestStreak
    }
}
