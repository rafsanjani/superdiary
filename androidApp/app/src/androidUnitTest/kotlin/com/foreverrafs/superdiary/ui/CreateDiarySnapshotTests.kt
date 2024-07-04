package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.Paparazzi
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("Because this test is failing for 1.7.0-alpha01 of compose multiplatform")
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
            SuperdiaryTheme {
                CreateDiaryScreenContent(
                    onNavigateBack = {},
                    isGeneratingFromAi = false,
                    onGenerateAI = { _: String, _: Int -> },
                    richTextState = rememberRichTextState().apply {},
                    onSaveDiary = {},
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - very few words`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                CreateDiaryScreenContent(
                    onNavigateBack = {},
                    isGeneratingFromAi = false,
                    onGenerateAI = { _: String, _: Int -> },
                    richTextState = rememberRichTextState().apply {
                        setText(
                            "A four word line",
                        )
                    },
                    onSaveDiary = {},
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - with content`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                CreateDiaryScreenContent(
                    onNavigateBack = {},
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
                )
            }
        }
    }

    @Test
    fun `Create Diary Screen - with content - generating from AI`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                CreateDiaryScreenContent(
                    onNavigateBack = {},
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
                )
            }
        }
    }
}
