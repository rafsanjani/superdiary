package com.foreverrafs.superdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.android.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.android.navigation.BottomBar
import com.foreverrafs.superdiary.android.screens.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomBar(navController = navController)
                    },
                    topBar = {
                        SuperDiaryAppBar()
                    },
                ) {
                    Surface(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize()
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.app,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
