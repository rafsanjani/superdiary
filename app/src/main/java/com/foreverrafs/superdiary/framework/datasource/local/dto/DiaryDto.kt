package com.foreverrafs.superdiary.framework.datasource.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val message: String,

    @ColumnInfo(name = "date")
    val date: String
)