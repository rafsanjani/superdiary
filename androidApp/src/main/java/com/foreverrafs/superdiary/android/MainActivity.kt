package com.foreverrafs.superdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.android.screens.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DestinationsNavHost(navGraph = NavGraphs.app)
                }
            }
        }
    }
}
