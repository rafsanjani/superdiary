package com.foreverrafs.superdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.setSingletonImageLoaderFactory
import com.foreverrafs.superdiary.auth.register.AuthDeepLink
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryNavHost
import com.foreverrafs.superdiary.ui.navigation.getAsyncImageLoader
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

data class AppLaunchContext(
    val deepLink: AuthDeepLink? = null,
)

/**
 * Entry point into the whole app. In an ideal world we'll only just render
 * this composable on individual platforms but the world isn't ideal, huh!
 */

@Composable
fun App(
    modifier: Modifier = Modifier,
    launchContext: AppLaunchContext = AppLaunchContext(),
) {
    val appViewModel: AppViewModel = koinViewModel {
        parametersOf(launchContext)
    }
    val appViewState by appViewModel.viewState.collectAsStateWithLifecycle()

    SuperDiaryTheme {
        setSingletonImageLoaderFactory(::getAsyncImageLoader)
        SuperDiaryNavHost(
            viewState = appViewState,
            onOnboardingComplete = appViewModel::onOnboardingComplete,
            modifier = modifier,
        )
    }
}
