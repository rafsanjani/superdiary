package com.foreverrafs.superdiary.creatediary.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.permission.LocationManager
import com.foreverrafs.superdiary.core.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.permission.PermissionState
import com.foreverrafs.superdiary.core.permission.PermissionsControllerWrapper
import com.foreverrafs.superdiary.creatediary.AddDiaryUseCase
import com.foreverrafs.superdiary.data.Result
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.utils.DiarySettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateDiaryViewModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val diaryAI: DiaryAI,
    private val logger: AggregateLogger,
    private val locationManager: LocationManager,
    private val locationPermissionManager: LocationPermissionManager,
    private val preference: DiaryPreference,
) : ViewModel() {

    val permissionState: StateFlow<PermissionState> = locationPermissionManager
        .permissionState
        .stateIn(
            viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = PermissionState.NotDetermined,
        )

    val diarySettings: StateFlow<DiarySettings> = preference.settings.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(5000),
        DiarySettings.Companion.Empty,
    )

    private val _screenState: MutableStateFlow<CreateDiaryScreenState> = MutableStateFlow(
        CreateDiaryScreenState(),
    )

    val screenState: StateFlow<CreateDiaryScreenState> = _screenState
        .onStart { startLocationUpdates() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = CreateDiaryScreenState(),
        )

    private fun startLocationUpdates() = viewModelScope.launch {
        permissionState.collectLatest { state ->
            if (state == PermissionState.Granted) {
                logger.i(Tag) {
                    "Location permission granted. Requesting location updates"
                }
                locationManager.requestLocation(
                    onError = {
                        logger.e(Tag, it) {
                            "Error requesting location updates. Entry will not be tagged!"
                        }
                    },
                    onLocation = { latitude, longitude ->
                        logger.i(Tag) {
                            "Updating state with location [$latitude, $longitude]"
                        }

                        _screenState.update {
                            it.copy(location = Location(latitude, longitude))
                        }

                        logger.i(Tag) {
                            "Location updated. Cancelling updates!"
                        }
                        locationManager.stopRequestingLocation()
                    },
                )
            } else {
                logger.i(Tag) {
                    "Permission hasn't been granted"
                }
            }
        }
    }

    fun saveDiary(diary: Diary) = viewModelScope.launch {
        when (val addDiaryResult = addDiaryUseCase(diary)) {
            is Result.Failure -> {
                logger.e(Tag, addDiaryResult.error)
            }

            is Result.Success -> {
                logger.i(Tag) {
                    "Diary entry successfully saved: $diary"
                }
            }
        }
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    fun onRequestLocationPermission() = viewModelScope.launch {
        locationPermissionManager.provideLocationPermission()
    }

    // Workaround for getting a handle on the PermissionController for requesting
    // location permissions. Explored a few alternatives and settled with this
    // because it provided the most modular solution at a little cost.
    fun getPermissionsController(): PermissionsControllerWrapper =
        locationPermissionManager.getPermissionsController()

    fun onPermanentlyDismissLocationPermissionDialog() = viewModelScope.launch {
        preference.save {
            it.copy(showLocationPermissionDialog = false)
        }
    }

    companion object {
        private val Tag = CreateDiaryViewModel::class.simpleName.orEmpty()
    }
}
