package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.model.Streak
import com.foreverrafs.superdiary.diary.utils.toDate
import kotlinx.datetime.minus

class CalculateStreakUseCase {
    operator fun invoke(diaries: List<Diary>): Streak {
        var streak = Streak(
            count = 0,
            startDate = diaries.first().date.toDate(),
            endDate = diaries.first().date.toDate()
        )

        diaries
            .asSequence()
            .sortedBy { it.date }
            .forEach {
                streak = if ((it.date.toDate() - streak.endDate).days == 1) {
                    streak.copy(
                        count = streak.count + 1,
                        endDate = it.date.toDate()
                    )
                } else {
                    Streak(0, it.date.toDate(), it.date.toDate())
                }
            }

        return streak
    }
}
