package com.foreverrafs.superdiary.business.data

import com.foreverrafs.superdiary.business.Result
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.Repository
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

    override fun getAllDiaries(): Flow<Result<List<Diary>>> {
        return flowOf(Result.Success(diaryList.toList()))
    }

    override fun searchDiary(query: String): Flow<Result<Diary>> = flow {
        val data = diaryList.firstOrNull { it.message.contains(query) }

        if (data == null) {
            emit(Result.Error(Exception("Diary not found")))
        } else {
            emit(Result.Success(data = data))
        }
    }
}