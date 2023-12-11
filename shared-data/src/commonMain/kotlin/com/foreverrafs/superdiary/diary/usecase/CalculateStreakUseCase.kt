package com.foreverrafs.superdiary.diary.usecase

import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.model.Streak
import com.foreverrafs.superdiary.diary.utils.toDate
import kotlinx.datetime.minus

class CalculateStreakUseCase {
    operator fun invoke(diaries: List<Diary>): Streak {
        val streak = diaries
            .asSequence()
            .sortedBy { it.date }
            .windowed(size = 2, step = 1)
            .filter { (first, second) ->
                (second.date.toDate() - first.date.toDate()).days == 1
            }

        return Streak(
            count = streak.count(),
            dates = listOf(
                streak.firstOrNull()?.firstOrNull()?.date?.toDate(),
                streak.lastOrNull()?.lastOrNull()?.date?.toDate(),
            ),
        )
    }
}
