package com.foreverrafs.superdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.foreverrafs.superdiary.android.di.ApplicationComponent
import com.foreverrafs.superdiary.ui.App

class MainActivity : ComponentActivity() {
    private val appComponent by lazy {
        ApplicationComponent.from(this)
    }

    private val appScreens by lazy {
        appComponent.appScreens
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}
