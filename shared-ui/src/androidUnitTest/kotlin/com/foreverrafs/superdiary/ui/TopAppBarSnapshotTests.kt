package com.foreverrafs.superdiary.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.Paparazzi
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.design.components.AppBar
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class TopAppBarSnapshotTests(
    @param:TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        useDeviceResolution = true,
    )

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun `Top App Bar`() {
        paparazzi.snapshot {
            SharedTransitionLayout {
                SuperDiaryPreviewTheme {
                    Scaffold(
                        topBar = {
                            AppBar()
                        },
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Cyan)
                                .padding(it),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("hello World")
                        }
                    }
                }
            }
        }
    }
}
