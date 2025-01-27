package com.foreverrafs.auth

import android.annotation.SuppressLint
import android.content.Context

/**
 * Provides an Android context for the given scope. When used for an
 * Activity Context, a call to setContext should always be followed by a
 * preceding call to clearContext() to prevent the activity from getting
 * leaked.
 *
 * This should only be used from the Application class where it can be
 * cleared in a structured manner
 */
class AndroidContextProvider private constructor() {
    private var context: Context? = null
    fun getContext(): Context? = context

    companion object {
        @SuppressLint("StaticFieldLeak")
        private val instance = AndroidContextProvider()
        fun getInstance(): AndroidContextProvider = instance
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun clearContext() {
        context = null
    }
}
