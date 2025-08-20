package com.foreverrafs.superdiary.domain.usecase

import com.foreverrafs.superdiary.common.utils.AppCoroutineDispatchers
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.utils.toDate
import kotlinx.coroutines.withContext
import kotlinx.datetime.minus

class CalculateStreakUseCase(
    private val dispatchers: AppCoroutineDispatchers,
) {
    suspend operator fun invoke(diaries: List<Diary>): Streak = withContext(dispatchers.computation) {
        var streak = Streak(
            count = 0,
            startDate = diaries.first().date.toDate(),
            endDate = diaries.first().date.toDate(),
        )

        diaries
            .asSequence()
            .sortedBy { it.date }
            .forEach {
                streak = if ((it.date.toDate() - streak.endDate).days == 1) {
                    streak.copy(
                        count = streak.count + 1,
                        endDate = it.date.toDate(),
                    )
                } else {
                    Streak(0, it.date.toDate(), it.date.toDate())
                }
            }

        streak
    }
}
