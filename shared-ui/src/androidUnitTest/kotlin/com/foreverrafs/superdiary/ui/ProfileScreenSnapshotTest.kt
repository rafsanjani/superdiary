package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.superdiary.ui.feature.profile.ProfileScreenViewData
import com.foreverrafs.superdiary.ui.feature.profile.screen.ProfileScreenContent
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ProfileScreenSnapshotTest(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        useDeviceResolution = true,
    )

    @Test
    fun `Profile Screen - Idle`() {
        paparazzi.snapshot {
            SuperdiaryPreviewTheme {
                ProfileScreenContent(
                    viewState = ProfileScreenViewData(
                        name = "Rafsanjani Aziz",
                        email = "foreverrafs@gmail.com",
                        avatarUrl = "",
                    ),
                    onConsumeErrorMessage = {},
                    isLogoutDialogVisible = false,
                    onLogout = {},
                    onLogoutDialogVisibilityChange = {},
                )
            }
        }
    }

    @Test
    fun `Profile Screen - Logout dialog visible`() {
        paparazzi.snapshot {
            SuperdiaryPreviewTheme {
                ProfileScreenContent(
                    viewState = ProfileScreenViewData(
                        name = "Rafsanjani Aziz",
                        email = "foreverrafs@gmail.com",
                        avatarUrl = "",
                    ),
                    onConsumeErrorMessage = {},
                    isLogoutDialogVisible = true,
                    onLogout = {},
                    onLogoutDialogVisibilityChange = {},
                )
            }
        }
    }

    @Test
    fun `Profile Screen - error message visible`() {
        paparazzi.snapshot {
            SuperdiaryPreviewTheme {
                ProfileScreenContent(
                    viewState = ProfileScreenViewData(
                        name = "Rafsanjani Aziz",
                        email = "foreverrafs@gmail.com",
                        avatarUrl = "",
                        errorMessage = "Something went wrong",
                    ),
                    onConsumeErrorMessage = {},
                    isLogoutDialogVisible = false,
                    onLogout = {},
                    onLogoutDialogVisibilityChange = {},
                )
            }
        }
    }
}
