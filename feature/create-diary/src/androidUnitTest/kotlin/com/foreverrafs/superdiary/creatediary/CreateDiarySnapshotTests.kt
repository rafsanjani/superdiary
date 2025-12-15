package com.foreverrafs.superdiary.creatediary

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import dev.icerock.moko.permissions.PermissionState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalSharedTransitionApi::class)
@RunWith(TestParameterInjector::class)
class CreateDiarySnapshotTests(
    @param:TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        useDeviceResolution = true,
    )

    @Composable
    private fun SnapshotContent(
        isGeneratingFromAi: Boolean = false,
        showLocationPermissionRationale: Boolean = false,
        permissionState: PermissionState = PermissionState.NotDetermined,
        avatarUrl: String? = null,
        richTextState: RichTextState = rememberRichTextState(),
        showSaveDialog: Boolean = false,
    ) {
        CreateDiaryScreenContent(
            isGeneratingFromAi = isGeneratingFromAi,
            onGenerateAI = { _: String, _: Int -> },
            richTextState = richTextState,
            onSaveDiary = {},
            onDontAskAgain = {},
            showLocationPermissionRationale = showLocationPermissionRationale,
            onRequestLocationPermission = {},
            permissionState = permissionState,
            avatarUrl = avatarUrl,
            showSaveDialog = showSaveDialog,
            onShowSaveDialogChange = {},
            onNavigateBack = {},
        )
    }

    @Test
    fun `Create Diary Screen - empty`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SnapshotContent(
                    isGeneratingFromAi = false,
                    showLocationPermissionRationale = false,
                    permissionState = PermissionState.NotDetermined,
                    avatarUrl = null,
                    showSaveDialog = false,
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - very few words`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SnapshotContent(
                    isGeneratingFromAi = false,
                    richTextState = rememberRichTextState().apply {
                        setText("Hello World!")
                    },
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - with content`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SnapshotContent(
                    isGeneratingFromAi = false,
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
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - with content - generating from AI`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SnapshotContent(
                    isGeneratingFromAi = true,
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
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - Location Permission Dialog`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SnapshotContent(
                    showLocationPermissionRationale = true,
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - Confirm Save Dialog`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                SnapshotContent(
                    showLocationPermissionRationale = false,
                    showSaveDialog = true,
                )
            }
        }
    }
}
