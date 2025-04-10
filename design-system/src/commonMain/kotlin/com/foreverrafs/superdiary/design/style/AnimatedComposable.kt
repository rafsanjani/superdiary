package com.foreverrafs.superdiary.design.style

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import kotlin.reflect.KType

inline fun <reified T : Any> NavGraphBuilder.animatedComposable(
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) = composable<T>(
    content = content,
    enterTransition = { enterTransition() },
    exitTransition = { exitTransition() },
    popEnterTransition = { enterTransition() },
    popExitTransition = { exitTransition() },
    typeMap = typeMap,
    deepLinks = deepLinks,
)

fun enterTransition() = fadeIn(
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing,
    ),
) + scaleIn(
    initialScale = 0.95f,
    animationSpec = tween(300, easing = FastOutSlowInEasing),
)

fun exitTransition() = fadeOut(
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing,
    ),
) + scaleOut(
    targetScale = 1.05f,
    animationSpec = tween(300, easing = FastOutSlowInEasing),
)
