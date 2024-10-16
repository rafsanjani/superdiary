package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.foreverrafs.superdiary.core.location.BindEffect
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.location.permission.PermissionState
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.utils.DiarySettings
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryScreen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object CreateDiaryScreen : SuperDiaryScreen {

    @Composable
    fun Content(navController: NavController) {
        val viewModel: CreateDiaryViewModel = koinInject()

        val undoManager = rememberUndoableRichTextState()
        val richTextState = undoManager.richTextState
        val coroutineScope = rememberCoroutineScope()

        val screenState by viewModel.screenState.collectAsStateWithLifecycle()
        val locationPermissionState by viewModel.permissionState.collectAsStateWithLifecycle()
        val settings by viewModel.diarySettings.collectAsStateWithLifecycle(DiarySettings.Empty)

        var isGeneratingFromAI by remember {
            mutableStateOf(false)
        }

        var showLocationPermissionRationale by remember(settings, locationPermissionState) {
            mutableStateOf(
                locationPermissionState != PermissionState.Granted &&
                    settings.showLocationPermissionDialog,
            )
        }

        BindEffect(
            viewModel.getPermissionsController(),
        )

        CreateDiaryScreenContent(
            onNavigateBack = navController::popBackStack,
            richTextState = richTextState,
            isGeneratingFromAi = isGeneratingFromAI,
            showLocationPermissionRationale = showLocationPermissionRationale,
            onGenerateAI = { prompt, wordCount ->
                undoManager.save()
                var generatedWords = ""

                coroutineScope.launch {
                    viewModel
                        .generateAIDiary(
                            prompt = prompt,
                            wordCount = wordCount,
                        )
                        .onStart {
                            isGeneratingFromAI = true
                            richTextState.setHtml("<p>Generating diary...</p>")
                        }
                        .catch {
                            isGeneratingFromAI = false
                            richTextState.setHtml("<p style=\"color:red\">Error generating entry</p>")
                        }
                        .onCompletion {
                            isGeneratingFromAI = false
                        }
                        .collect { chunk ->
                            generatedWords += chunk
                            richTextState.setHtml("<p>$generatedWords</p>")
                        }
                }
            },
            onSaveDiary = { entry ->
                viewModel.saveDiary(
                    Diary(
                        entry = entry,
                        date = Clock.System.now(),
                        isFavorite = false,
                        location = screenState.location ?: Location.Empty,
                    ),
                )

                navController.popBackStack()
            },
            onRequestLocationPermission = {
                showLocationPermissionRationale = false
                viewModel.provideLocationPermission()
            },
            permissionState = locationPermissionState,
            onDontAskAgain = {
                showLocationPermissionRationale = false
                viewModel.onPermanentlyDismissLocationPermissionDialog()
            },
        )
    }
}
