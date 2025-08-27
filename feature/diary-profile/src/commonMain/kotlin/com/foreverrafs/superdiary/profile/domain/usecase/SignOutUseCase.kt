package com.foreverrafs.superdiary.profile.domain.usecase

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.domain.usecase.ClearDiariesUseCase

class SignOutUseCase(
    private val authApi: AuthApi,
    private val dataSource: DataSource,
    private val preferences: DiaryPreference,
    private val clearDiariesUseCase: ClearDiariesUseCase,
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authApi.signOut()
            preferences.clear()
            clearDiariesUseCase()
            dataSource.clearChatMessages()

            Result.success(Unit)
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }
}
