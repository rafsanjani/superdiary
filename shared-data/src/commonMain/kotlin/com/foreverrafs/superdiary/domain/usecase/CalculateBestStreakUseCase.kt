package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.utils.toDate
import kotlinx.coroutines.withContext
import kotlinx.datetime.minus

class CalculateBestStreakUseCase(
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diaries: List<Diary>): Streak =
        withContext(dispatchers.computation) {
            var streak = Streak(
                count = 0,
                startDate = diaries.first().date.toDate(),
                endDate = diaries.first().date.toDate(),
            )

            var bestStreak = streak

            diaries
                .asSequence()
                .sortedBy { it.date }
                .forEach {
                    streak = if ((it.date.toDate() - streak.endDate).days == 1) {
                        val currentStreak = streak.copy(
                            count = streak.count + 1,
                            endDate = it.date.toDate(),
                        )

                        if (currentStreak.count > bestStreak.count) {
                            bestStreak = currentStreak
                        }
                        currentStreak
                    } else {
                        Streak(count = 0, startDate = it.date.toDate(), endDate = it.date.toDate())
                    }
                }

            bestStreak
        }
}
