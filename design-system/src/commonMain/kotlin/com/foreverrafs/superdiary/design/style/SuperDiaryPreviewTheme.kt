package com.foreverrafs.superdiary.design.style

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SuperDiaryPreviewTheme(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    SuperDiaryTheme(darkTheme = darkTheme) {
        Scaffold(
            modifier = modifier,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                color = MaterialTheme.colorScheme.background,
            ) {
                SharedTransitionLayout {
                    AnimatedContent(targetState = Unit) {
                        CompositionLocalProvider(
                            LocalInspectionMode provides true,
                            LocalSharedTransitionScope provides this@SharedTransitionLayout,
//                            LocalAnimatedContentScope provides this,
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
