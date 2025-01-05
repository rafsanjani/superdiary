package com.foreverrafs.superdiary.profile.domain.usecase

import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.profile.domain.repository.ProfileRepository

class GetCurrentUserUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): Result<UserInfo> = repository.getUserInfo()
}
