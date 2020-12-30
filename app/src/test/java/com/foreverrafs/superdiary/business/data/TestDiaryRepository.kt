package com.foreverrafs.superdiary.business.data

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestDiaryRepository(
    private val diaryList: MutableList<Diary>
) : DataSource {
    override suspend fun add(diary: Diary): Long {
        return if (diaryList.add(diary)) 1 else 0
    }

    override suspend fun delete(diary: Diary): Int {
        return if (diaryList.remove(diary))
            1
        else
            0
    }

    override fun getAllDiaries(): Flow<List<Diary>> {
        return flow {
            emit(diaryList)
        }
    }
}