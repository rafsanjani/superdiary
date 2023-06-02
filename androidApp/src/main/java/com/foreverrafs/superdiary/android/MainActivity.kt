package com.foreverrafs.superdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.android.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.android.di.ApplicationComponent
import com.foreverrafs.superdiary.android.navigation.AppNavigation
import com.foreverrafs.superdiary.android.navigation.BottomBar
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

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
            AppTheme {
                val navController = rememberAnimatedNavController()
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
                        AppNavigation(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController,
                            appScreens = appScreens,
                        )
                    }
                }
            }
        }
    }
}
