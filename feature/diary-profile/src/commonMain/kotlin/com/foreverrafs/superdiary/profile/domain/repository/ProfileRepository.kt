package com.foreverrafs.superdiary.profile.domain.repository

import com.foreverrafs.auth.model.UserInfo

interface ProfileRepository {
    suspend fun getUserInfo(): Result<UserInfo>
}
