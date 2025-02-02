package com.foreverrafs.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String,
    val avatarUrl: String,
    val name: String,
    val email: String,
)
