package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.location.LocationManager
import com.foreverrafs.superdiary.core.location.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.location.permission.PermissionState
import com.foreverrafs.superdiary.core.location.permission.PermissionsControllerWrapper
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.data.utils.DiarySettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateDiaryViewModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val diaryAI: DiaryAI,
    private val logger: AggregateLogger,
    private val locationManager: LocationManager,
    private val locationPermissionManager: LocationPermissionManager,
    private val preference: DiaryPreference,
) : ViewModel() {

    val permissionState = locationPermissionManager.permissionState

    val diarySettings: StateFlow<DiarySettings> = preference.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        preference.snapshot,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<CreateDiaryScreenState> = permissionState
        .flatMapConcat { state ->
            // We don't want to start updating location until permissions have been granted.
            if (state == PermissionState.Granted) {
                locationManager.requestLocation()
            } else {
                logger.i(Tag) {
                    "Permission hasn't been granted yet. Emitting an empty flow"
                }
                emptyFlow()
            }
        }
        .map {
            CreateDiaryScreenState(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CreateDiaryScreenState(location = Location.Empty),
        )

    fun saveDiary(diary: Diary) = viewModelScope.launch {
        addDiaryUseCase(diary)
        logger.i(Tag) {
            "Diary entry successfully saved: $diary"
        }
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    fun provideLocationPermission() = viewModelScope.launch {
        locationPermissionManager.provideLocationPermission()
    }

    // Workaround for getting a handle on the PermissionController for requesting
    // location permissions. Explored a few alternatives and settled with this
    // because it provided the most modular solution at a little cost.
    fun getPermissionsController(): PermissionsControllerWrapper =
        locationPermissionManager.getPermissionsController()

    fun onPermanentlyDismissLocationPermissionDialog() = viewModelScope.launch {
        val currentDiarySettings = preference.getSnapshot()

        preference.save(
            currentDiarySettings.copy(showLocationPermissionDialog = false),
        )
    }

    companion object {
        private val Tag = CreateDiaryViewModel::class.simpleName.orEmpty()
    }
}
