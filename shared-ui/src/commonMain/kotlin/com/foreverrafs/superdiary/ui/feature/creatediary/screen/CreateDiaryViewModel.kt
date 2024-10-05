package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.core.location.LocationManager
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateDiaryViewModel(
    private val addDiaryUseCase: AddDiaryUseCase,
    private val diaryAI: DiaryAI,
    private val logger: AggregateLogger,
    private val locationManager: LocationManager,
) : ViewModel() {

    private val locationUpdateTrigger: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val screenState: StateFlow<CreateDiaryScreenState> = locationUpdateTrigger
        .flatMapLatest { granted ->
            // We don't want to start updating location until permissions have been granted.
            // Trigger wil be incremented when permission is granted making it safe to request location
            if (granted) {
                locationManager.requestLocation()
            } else {
                emptyFlow()
            }
        }
        .map {
            CreateDiaryScreenState(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CreateDiaryScreenState(),
        )

    fun saveDiary(diary: Diary) = viewModelScope.launch {
        addDiaryUseCase(diary)
        logger.i(Tag) {
            "Diary entry successfully saved: $diary"
        }
    }

    fun onLocationPermissionGranted(granted: Boolean) {
        locationUpdateTrigger.update {
            granted
        }
    }

    fun generateAIDiary(prompt: String, wordCount: Int): Flow<String> =
        diaryAI.generateDiary(prompt, wordCount)

    companion object {
        private val Tag = CreateDiaryViewModel::class.simpleName.orEmpty()
    }
}
