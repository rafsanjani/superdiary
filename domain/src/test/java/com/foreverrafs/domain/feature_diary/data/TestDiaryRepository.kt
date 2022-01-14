package com.foreverrafs.domain.feature_diary.data

import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class TestDiaryRepository(
    private val diaryList: MutableList<Diary>
) : Repository {
    override suspend fun add(diary: Diary): Result<Long> {
        return if (diaryList.add(diary)) Result.Success(1) else Result.Error(Exception("Error saving diary"))
    }

    override suspend fun delete(diary: Diary): Result<Int> {
        return if (diaryList.remove(diary))
            Result.Success(1)
        else
            Result.Error(Exception("Error deleting diary"))
    }

    override suspend fun getAllDiaries(): Flow<Result<List<Diary>>> {
        return flowOf(Result.Success(diaryList.toList()))
    }

    override suspend fun searchDiary(title: String): Flow<Result<List<Diary>>> = flow {
        val data = diaryList.filter { it.message.contains(title) }

        if (data.isNotEmpty()) {
            emit(Result.Error(Exception("Diary not found")))
        } else {
            emit(Result.Success(data = data))
        }
    }
}