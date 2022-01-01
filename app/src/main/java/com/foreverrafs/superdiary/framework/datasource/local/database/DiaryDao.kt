package com.foreverrafs.superdiary.framework.datasource.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.foreverrafs.superdiary.framework.datasource.local.dto.DiaryDto
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert(onConflict = REPLACE)
    suspend fun add(diary: DiaryDto): Long

    @Delete
    suspend fun delete(diary: DiaryDto): Int

    @Query("SELECT  * FROM diary")
    fun getAllDiaries(): Flow<List<DiaryDto>>

    @Query("SELECT * from diary WHERE title LIKE :query")
    fun searchDiary(query: String): Flow<DiaryDto>
}