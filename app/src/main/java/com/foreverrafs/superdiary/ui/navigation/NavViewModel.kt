package com.foreverrafs.superdiary.ui.navigation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavViewModel : ViewModel() {
    data class NavEvent(val destination: String)

    private var _event: MutableStateFlow<NavEvent?> = MutableStateFlow(value = null)
    val event = _event.asSharedFlow()

    fun onNavEvent(event: NavEvent?) {
        _event.value = event
    }
}