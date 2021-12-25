package com.foreverrafs.superdiary.framework.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.foreverrafs.superdiary.framework.datasource.local.dto.DiaryDto

@Database(entities = [DiaryDto::class], version = 1)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao

    companion object {
        const val DATABASE_NAME = "diary.db"
    }
}