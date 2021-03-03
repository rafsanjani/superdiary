package com.foreverrafs.superdiary.data

import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.repository.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockDataSource : DataSource {

    override suspend fun add(diary: Diary): Long {
        val id = Random.nextLong()

        mockDiaries.add(diary.copy(id = id))
        return id
    }

    override suspend fun delete(diary: Diary): Int {
        mockDiaries.remove(diary)
        return diary.id.toInt()
    }

    override fun getAllDiaries(): Flow<List<Diary>> {
        return flow {
            emit(mockDiaries)
        }
    }
}