package com.foreverrafs.superdiary.core.permission

import androidx.activity.ComponentActivity

/**
 * Only used on Android. Allows the permission controller to bind to the
 * instance of the underlying activity housing the screen
 */
fun interface Bindable {
    fun bind(activity: ComponentActivity)
}
