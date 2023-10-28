package com.foreverrafs.superdiary.diary.generator

import kotlinx.coroutines.flow.Flow

fun interface DiaryAI {
    fun generateDiary(prompt: String, wordCount: Int): Flow<String>
}
