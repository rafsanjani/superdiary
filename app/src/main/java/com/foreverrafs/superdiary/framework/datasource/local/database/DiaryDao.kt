package com.foreverrafs.superdiary.framework.datasource.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.foreverrafs.superdiary.framework.datasource.local.model.DiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert(onConflict = REPLACE)
    suspend fun add(diary: DiaryEntity): Long

    @Delete
    suspend fun delete(diary: DiaryEntity): Int

    @Query("SELECT  * FROM diary")
    fun getAllDiaries(): Flow<List<DiaryEntity>>
}