package com.foreverrafs.auth.model

import kotlinx.datetime.Instant

data class SessionInfo(
    val expiresAt: Instant,
    val accessToken: String,
    val refreshToken: String,
    val userInfo: UserInfo?,
)
