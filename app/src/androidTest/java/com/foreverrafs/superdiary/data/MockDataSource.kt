package com.foreverrafs.superdiary.data

import com.foreverrafs.domain.business.model.Diary
import com.foreverrafs.domain.business.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockDataSource : com.foreverrafs.domain.business.repository.DataSource {

    override suspend fun add(diary: com.foreverrafs.domain.business.model.Diary): Long {
        val id = Random.nextLong()

        mockDiaries.add(diary.copy(id = id))
        return id
    }

    override suspend fun delete(diary: com.foreverrafs.domain.business.model.Diary): Int {
        mockDiaries.remove(diary)
        return diary.id.toInt()
    }

    override fun getAllDiaries(): Flow<List<com.foreverrafs.domain.business.model.Diary>> {
        return flow {
            emit(mockDiaries)
        }
    }

    override fun searchDiary(query: String): Flow<com.foreverrafs.domain.business.model.Diary>  = flow{
        emit(mockDiaries.first())
    }
}