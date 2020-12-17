package com.foreverrafs.superdiary.framework.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    private val _viewState: MutableStateFlow<T?> = MutableStateFlow(null)

    val viewState = _viewState.asLiveData()

    fun setViewState(state: T) {
        _viewState.value = state
    }
}