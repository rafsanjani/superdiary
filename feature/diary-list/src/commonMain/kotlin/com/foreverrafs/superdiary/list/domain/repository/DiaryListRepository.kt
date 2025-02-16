package com.foreverrafs.superdiary.list.domain.repository

import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryListRepository {
    fun getAllDiaries(): Flow<List<Diary>>
    fun getDiaryById(id: Long): Diary?
    suspend fun deleteDiaries(diary: List<Diary>): Result<Int>
    suspend fun updateDiary(diary: Diary): Result<Boolean>
}
