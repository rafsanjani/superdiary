package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.setSingletonImageLoaderFactory
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryNavHost
import com.foreverrafs.superdiary.ui.navigation.getAsyncImageLoader
import org.koin.compose.viewmodel.koinViewModel

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(modifier: Modifier = Modifier) {
    val appViewModel: AppViewModel = koinViewModel()
    val appViewState by appViewModel.viewState.collectAsStateWithLifecycle()

    SuperDiaryTheme {
        setSingletonImageLoaderFactory(::getAsyncImageLoader)
        SuperDiaryNavHost(
            viewState = appViewState,
            modifier = modifier,
        )
    }
}
