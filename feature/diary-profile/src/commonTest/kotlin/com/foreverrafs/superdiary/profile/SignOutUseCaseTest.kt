package com.foreverrafs.superdiary.profile

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.profile.domain.usecase.SignOutUseCase
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest

class SignOutUseCaseTest {

    // Using dev.mokkery.mock() to create mocks
    private val authApi: AuthApi = mock()
    private val dataSource: DataSource = mock()
    private val preferences: DiaryPreference = mock()

    private lateinit var signOutUseCase: SignOutUseCase

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        // Re-initialize the use case before each test
        signOutUseCase = SignOutUseCase(authApi, dataSource, preferences)

        // Stub the default successful behavior for all dependencies
        // coJustRun is the same in Mokkery for Unit-returning suspend functions
        everySuspend { authApi.signOut() } returns (Result.success(Unit))
        everySuspend { preferences.clear() } returns (Unit)
        everySuspend { dataSource.deleteAll() } returns (Unit)
        everySuspend { dataSource.clearChatMessages() } returns (Unit)
    }

    @Test
    fun `should complete successfully when sign out succeeds`() =
        runTest(testDispatcher) {
            val result = signOutUseCase.invoke()

            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(Unit)
        }

    @Test
    fun `should return failure and stop when authApi signOut fails`() =
        runTest(testDispatcher) {
            val exception = Exception("Auth API failure")

            everySuspend { authApi.signOut() } throws exception

            val result = signOutUseCase.invoke()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        }

    @Test
    fun `should return failure and stop when preferences clearing fails`() =
        runTest(testDispatcher) {
            val exception = Exception("Preferences clear failure")
            everySuspend { preferences.clear() } throws exception

            val result = signOutUseCase.invoke()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        }

    @Test
    fun `should return failure and stop when dataSource deletion fails`() =
        runTest(testDispatcher) {
            val exception = Exception("DataSource deleteAll failure")
            everySuspend { dataSource.deleteAll() } throws exception

            val result = signOutUseCase.invoke()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        }

    @Test
    fun `should return failure when clearing chat messages fails`() =
        runTest(testDispatcher) {
            val exception = Exception("DataSource clearChatMessages failure")
            everySuspend { dataSource.clearChatMessages() } throws exception

            val result = signOutUseCase.invoke()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        }

    @Test
    fun `should return failure when sign out operation fails`() =
        runTest(testDispatcher) {
            val exception = Exception("Auth API failure")
            everySuspend { authApi.signOut() } throws exception

            val result = signOutUseCase.invoke()

            verifySuspend(VerifyMode.exactly(1)) { authApi.signOut() }

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(exception)
        }

    @Test
    fun `should return failure when an unexpected exception occurs`() =
        runTest(testDispatcher) {
            val unexpectedException = IllegalStateException("Something truly went wrong!")
            everySuspend { preferences.clear() } throws unexpectedException

            val result = signOutUseCase.invoke()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(unexpectedException)
        }
}
