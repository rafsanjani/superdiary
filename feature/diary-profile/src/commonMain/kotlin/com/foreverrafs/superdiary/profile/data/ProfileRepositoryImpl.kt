package com.foreverrafs.superdiary.profile.data

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val authApi: AuthApi,
) : ProfileRepository {
    override suspend fun getUserInfo(): Result<UserInfo> {
        val currentUser = authApi.currentUserOrNull()

        return if (currentUser != null) {
            Result.success(currentUser)
        } else {
            Result.failure(Exception("User not found"))
        }
    }
}
