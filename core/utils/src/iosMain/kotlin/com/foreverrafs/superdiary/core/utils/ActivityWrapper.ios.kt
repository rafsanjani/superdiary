package com.foreverrafs.superdiary.core.utils

import androidx.compose.runtime.Composable

// no-op on ios
actual class ActivityWrapper

@Composable
actual fun localActivityWrapper(): ActivityWrapper? = ActivityWrapper()
