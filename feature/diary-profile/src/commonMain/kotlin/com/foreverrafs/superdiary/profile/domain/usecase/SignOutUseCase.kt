package com.foreverrafs.superdiary.profile.domain.usecase

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.domain.repository.DataSource

class SignOutUseCase(
    private val authApi: AuthApi,
    private val dataSource: DataSource,
    private val preferences: DiaryPreference,
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authApi.signOut()
            preferences.clear()
            dataSource.deleteAll()
            dataSource.clearChatMessages()

            Result.success(Unit)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }
}
