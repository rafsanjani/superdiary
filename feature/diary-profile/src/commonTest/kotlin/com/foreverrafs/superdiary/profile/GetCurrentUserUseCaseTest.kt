package com.foreverrafs.superdiary.profile

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.profile.domain.repository.ProfileRepository
import com.foreverrafs.superdiary.profile.domain.usecase.GetCurrentUserUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class GetCurrentUserUseCaseTest {

    private val repository = mock<ProfileRepository>()
    private val useCase = GetCurrentUserUseCase(repository)

    @Test
    fun `should return user info when repository succeeds`() = runTest {
        val expectedUser = UserInfo(
            id = "123",
            name = "Rafs",
            email = "rafs@example.com",
            avatarUrl = "",
            uniqueEmail = "",
        )

        everySuspend { repository.getUserInfo() } returns Result.success(expectedUser)

        val result = useCase.invoke()

        verifySuspend { repository.getUserInfo() }

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedUser)
    }

    @Test
    fun `should return failure when repository fails`() = runTest {
        val exception = Exception("Failed to fetch user info")
        everySuspend { repository.getUserInfo() } returns Result.failure(exception)

        val result = useCase.invoke()

        verifySuspend { repository.getUserInfo() }

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }
}
