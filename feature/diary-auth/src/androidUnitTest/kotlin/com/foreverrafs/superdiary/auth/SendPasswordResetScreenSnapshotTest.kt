package com.foreverrafs.superdiary.auth

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.auth.reset.PasswordResetViewState
import com.foreverrafs.superdiary.auth.reset.SendPasswordResetEmailScreenContent
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class SendPasswordResetScreenSnapshotTest(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        useDeviceResolution = true,
    )

    @Test
    fun `Password Reset Screen - Initial State`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SendPasswordResetEmailScreenContent(
                    viewState = PasswordResetViewState(),
                    onEmailChange = {},
                    onResetPasswordClick = {},
                    consumeTransientState = {},
                )
            }
        }
    }

    @Test
    fun `Password Reset Screen - Loading State`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SendPasswordResetEmailScreenContent(
                    viewState = PasswordResetViewState(
                        isLoading = true,
                    ),
                    onEmailChange = {},
                    onResetPasswordClick = {},
                    consumeTransientState = {},
                )
            }
        }
    }

    @Test
    fun `Password Reset Screen - Invalid Email State`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SendPasswordResetEmailScreenContent(
                    viewState = PasswordResetViewState(
                        isLoading = false,
                        isEmailValid = false,
                    ),
                    onEmailChange = {},
                    onResetPasswordClick = {},
                    consumeTransientState = {},
                )
            }
        }
    }

    @Test
    fun `Password Reset Screen - Email Sent State`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SendPasswordResetEmailScreenContent(
                    viewState = PasswordResetViewState(
                        isEmailSent = true,
                        isLoading = false,
                        email = "john.doe@gmail.com",
                    ),
                    onEmailChange = {},
                    onResetPasswordClick = {},
                    consumeTransientState = {},
                )
            }
        }
    }

    @Test
    fun `Password Reset Screen - Input Valid State`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SendPasswordResetEmailScreenContent(
                    viewState = PasswordResetViewState(
                        email = "john@doe.com",
                        isEmailValid = true,
                    ),
                    onEmailChange = {},
                    onResetPasswordClick = {},
                    consumeTransientState = {},
                )
            }
        }
    }

    @Test
    fun `Password Reset Screen - Input Invalid State`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SendPasswordResetEmailScreenContent(
                    viewState = PasswordResetViewState(
                        email = "john@",
                        isEmailValid = false,
                    ),
                    onEmailChange = {},
                    onResetPasswordClick = {},
                    consumeTransientState = {},
                )
            }
        }
    }
}
