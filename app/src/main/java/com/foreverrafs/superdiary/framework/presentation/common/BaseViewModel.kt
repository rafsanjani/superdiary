package com.foreverrafs.superdiary.framework.presentation.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T> : ViewModel() {
    private val _viewState: MutableStateFlow<T?> = MutableStateFlow(null)

    val viewState = _viewState.asStateFlow()

    fun setViewState(state: T) {
        _viewState.value = state
    }
}