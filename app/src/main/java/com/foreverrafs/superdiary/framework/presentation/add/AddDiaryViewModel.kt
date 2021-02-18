package com.foreverrafs.superdiary.framework.presentation.add

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.business.usecase.add.AddDiaryUseCase
import com.foreverrafs.superdiary.framework.presentation.add.state.AddDiaryState
import com.foreverrafs.superdiary.framework.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddDiaryViewModel
@Inject constructor(
    private val addDiary: AddDiaryUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val dataStore: DataStore<Preferences>,
) : BaseViewModel<AddDiaryState>() {


    companion object {
        private val KEY_DIARY_DRAFT = stringPreferencesKey("draft")
    }

    fun saveDiaryDraft(message: String) = viewModelScope.launch {
        dataStore.edit { settings ->
            settings[KEY_DIARY_DRAFT] = message
        }
    }

    fun clearDiaryDraft() = viewModelScope.launch {
        dataStore.edit {
            it.remove(KEY_DIARY_DRAFT)
        }
    }

    val diaryDraftEntry = dataStore.data.map { preferences ->
        preferences[KEY_DIARY_DRAFT]
    }

    fun saveDiary(diary: Diary) = viewModelScope.launch(dispatcher) {
        try {
            addDiary(diary)
            setViewState(AddDiaryState.Saved(diary))
        } catch (throwable: Throwable) {
            setViewState(AddDiaryState.Error(throwable))
        }
    }


}