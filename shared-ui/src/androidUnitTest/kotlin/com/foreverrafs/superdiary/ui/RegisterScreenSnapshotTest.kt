package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginScreen
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginViewState
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class RegisterScreenSnapshotTest(
    @TestParameter val snapshotDevice: SnapshotDevice,
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
            SuperdiaryPreviewTheme {
                LoginScreen(
                    viewState = LoginViewState.Idle,
                    onSignInSuccess = {},
                    onLoginClick = { _, _ -> },
                    onLoginWithGoogle = {},
                    onRegisterClick = {},
                )
            }
        }
    }

    @Test
    fun `Login Screen - Processing`() {
        paparazzi.snapshot {
            SuperdiaryPreviewTheme {
                LoginScreen(
                    viewState = LoginViewState.Processing,
                    onSignInSuccess = {},
                    onLoginClick = { _, _ -> },
                    onLoginWithGoogle = {},
                    onRegisterClick = {},
                )
            }
        }
    }
}
