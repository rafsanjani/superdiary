package com.foreverrafs.superdiary.ui

object SuperDiaryBackPressHandler {
    fun interface OnBackPressed {
        fun onBackPressed(): Boolean
    }

    private val callbacks = mutableSetOf<OnBackPressed>()
    fun addCallback(callback: OnBackPressed) {
        this.callbacks += callback
    }

    fun removeCallback(callback: OnBackPressed) {
        this.callbacks -= callback
    }

    fun execute(): Boolean = callbacks
        .map(OnBackPressed::onBackPressed)
        .all { it }

    fun clear() {
        callbacks.clear()
    }
}
