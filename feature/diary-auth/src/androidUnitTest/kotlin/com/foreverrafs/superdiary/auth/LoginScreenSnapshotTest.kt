package com.foreverrafs.superdiary.auth

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.auth.login.screen.LoginScreenContent
import com.foreverrafs.superdiary.auth.login.screen.LoginViewState
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class LoginScreenSnapshotTest(
    @param:TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        useDeviceResolution = true,
    )

    @Test
    fun `Login Screen - Idle`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                LoginScreenContent(
                    viewState = LoginViewState.Idle,
                    onSignInSuccess = {},
                    onLoginClick = { _, _ -> },
                    onLoginWithGoogle = {},
                    onRegisterClick = {},
                    isFromDeeplink = false,
                    onResetPasswordClick = {},
                )
            }
        }
    }

    @Test
    fun `Login Screen - Processing`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                LoginScreenContent(
                    viewState = LoginViewState.Processing,
                    onSignInSuccess = {},
                    onLoginClick = { _, _ -> },
                    onLoginWithGoogle = {},
                    onRegisterClick = {},
                    isFromDeeplink = false,
                    onResetPasswordClick = {},
                )
            }
        }
    }
}
