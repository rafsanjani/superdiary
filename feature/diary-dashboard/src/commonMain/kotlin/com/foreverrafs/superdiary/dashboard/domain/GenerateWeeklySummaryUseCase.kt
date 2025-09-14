package com.foreverrafs.superdiary.dashboard.domain

import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.dashboard.isInSameWeekAs
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.WeeklySummary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.utils.toDate
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class GenerateWeeklySummaryUseCase(
    private val logger: AggregateLogger,
    private val dataSource: DataSource,
    private val diaryAI: DiaryAI,
    private val clock: Clock,
) {
    private val today = clock.now().toDate()

    operator fun invoke(diaries: List<Diary>): Flow<String> = flow {
        logger.i(TAG) {
            "generateWeeklySummary: Fetching weekly summary for ${diaries.size} entries"
        }

        val previous: WeeklySummary? = dataSource.getOne()

        // Check if weekly summary was generated less than 7 days ago
        if (previous != null) {
            val difference = clock.now() - previous.date

            if (difference.inWholeDays <= 7L) {
                logger.i(TAG) {
                    "generateWeeklySummary: Weekly summary was generated ${difference.inWholeDays} days ago. Skip generation for now"
                }

                emit(previous.summary)
                return@flow
            }
        }

        val thisWeekEntries = diaries.filter { diary ->
            today.isInSameWeekAs(diary.date.toDate())
        }

        emitAll(
            flow = diaryAI
                .generateSummary(thisWeekEntries) {
                    logger.i(TAG) {
                        "generateWeeklySummary: Saving generated summary into database"
                    }
                    dataSource.save(it)
                }
                .catch { cause ->
                    logger.e(TAG, cause) {
                        "generateWeeklySummary: An error occurred generating weekly summary"
                    }
                    emit(ERROR_SUMMARY_TEXT)
                },
        )
    }

    companion object {
        private const val TAG = "GenerateWeeklySummaryUseCase"
        const val ERROR_SUMMARY_TEXT = "Error generating weekly summary"
    }
}
