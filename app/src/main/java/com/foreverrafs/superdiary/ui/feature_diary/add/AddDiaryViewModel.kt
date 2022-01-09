package com.foreverrafs.superdiary.ui.feature_diary.add

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foreverrafs.domain.feature_diary.Result
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.domain.feature_diary.usecase.AddDiaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddDiaryViewModel
@Inject
constructor(
    private val addDiary: AddDiaryUseCase,
    private val dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private var _viewState: MutableStateFlow<AddDiaryState?> = MutableStateFlow(null)
    private var viewEvent: MutableStateFlow<AddDiaryEvent?> = MutableStateFlow(null)

    val viewState: StateFlow<AddDiaryState?> = _viewState.asStateFlow()

    companion object {
        private val KEY_DIARY_DRAFT = stringPreferencesKey("draft")
    }

    init {
        viewModelScope.launch {
            viewEvent.collect {
                it?.let { event ->
                    when (event) {
                        is AddDiaryEvent.SaveDiary -> {
                            saveDiary(event.diary)
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            _viewState.collect {
                println("Collected")
            }
        }
    }

    fun saveDiaryDraft(message: String) = viewModelScope.launch {
        dataStore.edit { settings ->
            settings[KEY_DIARY_DRAFT] = message
        }
    }

    fun onEvent(event: AddDiaryEvent) {
        viewEvent.value = event
    }

    fun clearDiaryDraft() = viewModelScope.launch {
        dataStore.edit {
            it.remove(KEY_DIARY_DRAFT)
        }
    }

    val diaryDraftEntry = dataStore.data.map { preferences ->
        preferences[KEY_DIARY_DRAFT]
    }

    fun saveDiary(diary: Diary) =
        viewModelScope.launch(dispatcher) {
            when (val result = addDiary(diary)) {
                is Result.Error -> {
                    _viewState.value = AddDiaryState.Error(error = result.error)
                }
                is Result.Success -> {
                    _viewState.value = AddDiaryState.Success(diary)
                }
            }
        }
}