package com.foreverrafs.superdiary.ai.domain.model

import com.foreverrafs.superdiary.database.model.DiaryChatRoleDb

enum class DiaryChatRole {
    // User queries
    User,

    // AI Responses
    DiaryAI,

    // System instructions
    System,
}

fun DiaryChatRole.toDatabase() = DiaryChatRoleDb.valueOf(name)
