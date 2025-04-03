package com.foreverrafs.superdiary.auth

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import org.koin.java.KoinJavaComponent.inject

private val context: Context by inject(Context::class.java)

actual fun openMail() {
    try {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        // Fail silently if no email app is found on user's device
    }
}
