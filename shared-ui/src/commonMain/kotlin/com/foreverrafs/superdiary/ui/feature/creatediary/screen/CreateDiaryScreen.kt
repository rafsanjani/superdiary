package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryScreen
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
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
        val createDiaryViewModel: CreateDiaryViewModel = koinInject()

        val undoManager = rememberUndoableRichTextState()
        val richTextState = undoManager.richTextState
        val coroutineScope = rememberCoroutineScope()
        val screenState by createDiaryViewModel.screenState.collectAsStateWithLifecycle()

        val permissionsControllerFactory = rememberPermissionsControllerFactory()
        val permissionsController =
            remember(permissionsControllerFactory) { permissionsControllerFactory.createPermissionsController() }

        LaunchedEffect(Unit) {
            val isLocationPermissionGranted =
                permissionsController.isPermissionGranted(Permission.LOCATION)

            if (isLocationPermissionGranted) {
                createDiaryViewModel.onLocationPermissionGranted(isLocationPermissionGranted)
                return@LaunchedEffect
            }

            try {
                permissionsController.providePermission(Permission.LOCATION)
                // permission has been granted. request location update
                createDiaryViewModel.onLocationPermissionGranted(isLocationPermissionGranted)
            } catch (e: DeniedException) {
                println(e)
            } catch (e: DeniedAlwaysException) {
                println(e)
            }
        }

        BindEffect(permissionsController)

        var isGeneratingFromAI by remember {
            mutableStateOf(false)
        }

        CreateDiaryScreenContent(
            onNavigateBack = navController::popBackStack,
            richTextState = richTextState,
            isGeneratingFromAi = isGeneratingFromAI,
            onGenerateAI =
            { prompt, wordCount ->
                undoManager.save()
                var generatedWords = ""

                coroutineScope.launch {
                    createDiaryViewModel
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
                createDiaryViewModel.saveDiary(
                    Diary(
                        entry = entry,
                        date = Clock.System.now(),
                        isFavorite = false,
                    ),
                )

                navController.popBackStack()
            },
        )
    }
}
