package com.foreverrafs.superdiary.profile.data

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isSuccess
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.model.UserInfo
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class ProfileRepositoryImplTest {

    private val authApi: AuthApi = mock()
    private val repository = ProfileRepositoryImpl(authApi)

    @Test
    fun `getUserInfo returns success when current user exists`() = runTest {
        val user = UserInfo(
            id = "uid-1",
            name = "Rafs",
            email = "rafs@example.com",
            avatarUrl = "",
            uniqueEmail = ""
        )

        every { authApi.currentUserOrNull() } returns user

        val result = repository.getUserInfo()

        assertThat(result).isSuccess()
        assertThat(result.getOrNull()).isEqualTo(user)
    }

    @Test
    fun `getUserInfo returns failure when no current user`() = runTest {
        every { authApi.currentUserOrNull() } returns null

        val result = repository.getUserInfo()

        assertThat(result).isFailure()
    }
}
