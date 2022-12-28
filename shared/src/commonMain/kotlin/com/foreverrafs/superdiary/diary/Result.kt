package com.foreverrafs.superdiary.diary

import com.foreverrafs.superdiary.diary.model.Diary

sealed class Result {
    data class Success(val data: List<Diary>) : Result()
    data class Failure(val error: Throwable) : Result()
    object Loading: Result()
}