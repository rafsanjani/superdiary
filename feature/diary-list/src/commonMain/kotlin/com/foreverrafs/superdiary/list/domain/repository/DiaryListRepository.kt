package com.foreverrafs.superdiary.list.domain.repository

import androidx.paging.PagingData
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import kotlinx.coroutines.flow.Flow

interface DiaryListRepository {
    fun getAllDiaries(): Flow<PagingData<Diary>>
    fun getDiaryById(id: Long): Diary?
    suspend fun deleteDiaries(diaries: List<Diary>): Result<Int>
    suspend fun updateDiary(diary: Diary): Result<Boolean>
}
