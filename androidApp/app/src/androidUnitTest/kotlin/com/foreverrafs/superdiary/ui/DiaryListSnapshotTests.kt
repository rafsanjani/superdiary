package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import com.foreverrafs.superdiary.ui.style.SuperdiaryTheme
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.mohamedrejeb.richeditor.model.RichTextState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class DiaryListSnapshotTests {
    private val testClock = object : Clock {
        // 2023-11-10
        override fun now(): Instant = Instant.parse("2023-11-10T00:00:00.850951Z")
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = true,
        renderingMode = SessionParams.RenderingMode.NORMAL,
    )

    private val diaryListActions = DiaryListActions(
        onAddEntry = {},
        onDeleteDiaries = { true },
        onApplyFilters = {},
        onToggleFavorite = { false },
        onDiaryClicked = {},
    )

    @Test
    fun `Loading diary list`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Loading,
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Create diary entry`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                CreateDiaryScreenContent(
                    onNavigateBack = {},
                    onSaveDiary = {},
                    onGenerateAI = { _, _ -> },
                    isGeneratingFromAi = false,
                )
            }
        }
    }

    @Test
    fun `Create diary entry AI generated`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                CreateDiaryScreenContent(
                    richTextState = RichTextState().apply { setHtml("<p>AI generated diary content</p>") },
                    onNavigateBack = {},
                    onSaveDiary = {},
                    onGenerateAI = { _, _ -> },
                    isGeneratingFromAi = true,
                )
            }
        }
    }

//
//    fun Paparazzi.customSnapshot(
//        name: String? = null,
//        composable: @Composable () -> Unit,
//    ) {
//        val hostView = ComposeView(context)
//        // During onAttachedToWindow, AbstractComposeView will attempt to resolve its parent's
//        // CompositionContext, which requires first finding the "content view", then using that to
//        // find a root view with a ViewTreeLifecycleOwner
//        val parent = FrameLayout(context).apply { id = android.R.id.content }
//        parent.addView(hostView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        parent.layoutParams = FrameLayout.LayoutParams(
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//        )
//        hostView.setContent(composable)
//        snapshot(view = parent, name = name)
//    }

    @Test
    fun `Unfiltered non-empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        (0..5).map {
                            Diary(
                                id = it.toLong(),
                                entry = "Hello Diary $it",
                                date = testClock.now().minus(
                                    value = 1,
                                    unit = DateTimeUnit.MONTH,
                                    timeZone = TimeZone.UTC,
                                ),
                                isFavorite = false,
                            )
                        },
                        filtered = false,
                    ),
                    clock = testClock,
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Unfiltered empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        listOf(),
                        filtered = false,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Filtered empty diary list`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        listOf(),
                        filtered = true,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }

    @Test
    fun `Error loading diary list`() {
        paparazzi.snapshot {
            SuperdiaryTheme {
                DiaryListScreenContent(
                    state = DiaryListViewState.Error(
                        Error("Error loading diaries"),
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                )
            }
        }
    }
}
