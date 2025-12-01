package com.foreverrafs.superdiary.fakes

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.data.datasource.remote.DiaryApi
import com.foreverrafs.superdiary.data.model.DiaryDto
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

@OptIn(ExperimentalTime::class)
class FakeDiaryApi : DiaryApi {
    val clock: Clock = object : Clock {
        override fun now(): Instant = Instant.parse("2025-04-04T01:01:01.049Z")
    }

    override suspend fun countItems(): Result<Long> = Result.Success(initialDiaries.size.toLong())

    val initialDiaries = (0..10).map {
        DiaryDto(
            entry = "Fake Diary Entry ${it + 1}",
            id = it.toLong(),
            date = clock.now().plus(
                value = it + 1,
                unit = DateTimeUnit.DAY,
                timeZone = TimeZone.UTC,
            ),
            isFavorite = false,
            location = null,
        )
    }.toMutableList()

    private val diariesFlow = MutableStateFlow(initialDiaries)

    override fun fetchAll(): Flow<List<DiaryDto>> = diariesFlow.asStateFlow()

    override suspend fun save(diary: DiaryDto): Result<Boolean> {
        diariesFlow.update { currentDiaries ->
            val diaryToSave = if (diary.id != null) {
                diary
            } else {
                val nextId = (currentDiaries.mapNotNull { it.id }.maxOrNull() ?: -1L) + 1
                diary.copy(id = nextId)
            }

            val existingIndex = currentDiaries.indexOfFirst { it.id == diary.id }

            val newList = currentDiaries.toMutableList()
            if (existingIndex != -1) {
                newList[existingIndex] = diaryToSave
            } else {
                newList.add(diaryToSave)
            }
            newList
        }
        return Result.Success(true)
    }

    override suspend fun delete(diary: DiaryDto): Result<Boolean> {
        var removed = false

        diariesFlow.update { currentDiaries ->
            val newList = currentDiaries.toMutableList()
            removed = newList.removeIf { it.id == diary.id }
            newList
        }

        return if (removed) {
            Result.Success(true)
        } else {
            Result.Failure(
                Exception("Error deleting diary or diary not found: $diary"),
            )
        }
    }

    override suspend fun fetch(count: Int): Result<List<DiaryDto>> = Result.Success(initialDiaries)
}
