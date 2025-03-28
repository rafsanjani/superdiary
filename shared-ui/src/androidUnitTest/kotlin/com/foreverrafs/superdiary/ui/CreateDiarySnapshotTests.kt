package com.foreverrafs.superdiary.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import dev.icerock.moko.permissions.PermissionState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalSharedTransitionApi::class)
@RunWith(TestParameterInjector::class)
class CreateDiarySnapshotTests(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        useDeviceResolution = true,
    )

    @Test
    fun `Create Diary Screen - empty`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    CreateDiaryScreenContent(
                        isGeneratingFromAi = false,
                        onGenerateAI = { _: String, _: Int -> },
                        richTextState = rememberRichTextState().apply {},
                        onSaveDiary = {},
                        onDontAskAgain = {},
                        showLocationPermissionRationale = false,
                        onRequestLocationPermission = {},
                        permissionState = PermissionState.NotDetermined,
                        userInfo = null,
                        showSaveDialog = false,
                        onShowSaveDialogChange = {},
                        onNavigateBack = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@SuperDiaryPreviewTheme,
                    )
                }
            }
        }
    }

    @Test
    fun `Create Diary Screen - very few words`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    CreateDiaryScreenContent(
                        onNavigateBack = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@SuperDiaryPreviewTheme,
                        isGeneratingFromAi = false,
                        onGenerateAI = { _: String, _: Int -> },
                        richTextState = rememberRichTextState().apply {
                            setText(
                                "A four word line",
                            )
                        },
                        onSaveDiary = {},
                        showLocationPermissionRationale = false,
                        onRequestLocationPermission = {},
                        onDontAskAgain = {},
                        permissionState = PermissionState.NotGranted,
                        userInfo = null,
                        showSaveDialog = false,
                        onShowSaveDialogChange = {},
                    )
                }
            }
        }
    }

    @Test
    fun `Create Diary Screen - with content`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    CreateDiaryScreenContent(
                        isGeneratingFromAi = false,
                        onGenerateAI = { _: String, _: Int -> },
                        richTextState = rememberRichTextState().apply {
                            setText(
                                """
                                Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
                                when an unknown printer took a galley of type and scrambled it to make a type
                                specimen book. It has survived not only five centuries, but also the leap into
                                electronic typesetting, remaining essentially unchanged. It was popularised
                                in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                                and more recently with desktop publishing software like Aldus PageMaker including
                                versions of Lorem Ipsum.
                                """.trimIndent(),
                            )
                        },
                        onSaveDiary = {},
                        showLocationPermissionRationale = false,
                        onRequestLocationPermission = {},
                        onDontAskAgain = {},
                        permissionState = PermissionState.NotGranted,
                        userInfo = null,
                        showSaveDialog = false,
                        onShowSaveDialogChange = {},
                        onNavigateBack = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@SuperDiaryPreviewTheme,
                    )
                }
            }
        }
    }

    @Test
    fun `Create Diary Screen - with content - generating from AI`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    CreateDiaryScreenContent(
                        isGeneratingFromAi = true,
                        onGenerateAI = { _: String, _: Int -> },
                        richTextState = rememberRichTextState().apply {
                            setText(
                                """
                                Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,
                                when an unknown printer took a galley of type and scrambled it to make a type
                                specimen book. It has survived not only five centuries, but also the leap into
                                electronic typesetting, remaining essentially unchanged. It was popularised
                                in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                                and more recently with desktop publishing software like Aldus PageMaker including
                                versions of Lorem Ipsum.
                                """.trimIndent(),
                            )
                        },
                        onSaveDiary = {},
                        showLocationPermissionRationale = false,
                        onRequestLocationPermission = {},
                        onDontAskAgain = {},
                        permissionState = PermissionState.NotGranted,
                        userInfo = null,
                        showSaveDialog = false,
                        onShowSaveDialogChange = {},
                        onNavigateBack = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@SuperDiaryPreviewTheme,
                    )
                }
            }
        }
    }

    @Test
    fun `Create Diary Screen - Location Permission Dialog`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    CreateDiaryScreenContent(
                        isGeneratingFromAi = false,
                        onGenerateAI = { _: String, _: Int -> },
                        richTextState = rememberRichTextState().apply {},
                        onSaveDiary = {},
                        onDontAskAgain = {},
                        showLocationPermissionRationale = true,
                        onRequestLocationPermission = {},
                        permissionState = PermissionState.NotDetermined,
                        userInfo = null,
                        showSaveDialog = false,
                        onShowSaveDialogChange = {},
                        onNavigateBack = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@SuperDiaryPreviewTheme,
                    )
                }
            }
        }
    }

    @Test
    fun `Create Diary Screen - Confirm Save Dialog`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    CreateDiaryScreenContent(
                        isGeneratingFromAi = false,
                        onGenerateAI = { _: String, _: Int -> },
                        richTextState = rememberRichTextState().apply {},
                        onSaveDiary = {},
                        onDontAskAgain = {},
                        showLocationPermissionRationale = false,
                        onRequestLocationPermission = {},
                        permissionState = PermissionState.NotDetermined,
                        userInfo = null,
                        showSaveDialog = true,
                        onShowSaveDialogChange = {},
                        onNavigateBack = {},
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@SuperDiaryPreviewTheme,
                    )
                }
            }
        }
    }
}
