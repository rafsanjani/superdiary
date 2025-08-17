package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.location.BindEffect
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.location.permission.PermissionState
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryViewModel
import com.foreverrafs.superdiary.utils.DiarySettings
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlin.time.Clock
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CreateDiaryScreen(
    navController: NavHostController,
    userInfo: UserInfo?,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    val viewModel: CreateDiaryViewModel = koinViewModel()

    val richTextState = rememberRichTextState()
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
    var showSaveDialog by remember {
        mutableStateOf(false)
    }

    BindEffect(
        viewModel.getPermissionsController(),
    )

    CreateDiaryScreenContent(
        richTextState = richTextState,
        isGeneratingFromAi = isGeneratingFromAI,
        showLocationPermissionRationale = showLocationPermissionRationale,
        onGenerateAI = { prompt, wordCount ->
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
                    isMarkedForDelete = false,
                ),
            )

            navController.popBackStack()
        },
        onRequestLocationPermission = {
            showLocationPermissionRationale = false
            viewModel.onRequestLocationPermission()
        },
        permissionState = locationPermissionState,
        onDontAskAgain = {
            showLocationPermissionRationale = false
            viewModel.onPermanentlyDismissLocationPermissionDialog()
        },
        userInfo = userInfo,
        showSaveDialog = showSaveDialog,
        onShowSaveDialogChange = {
            showSaveDialog = it
        },
        onNavigateBack = navController::navigateUp,
    )
}
