package com.foreverrafs.superdiary.core.utils

import androidx.compose.runtime.Composable

expect class ActivityWrapper

@Composable
expect fun localActivityWrapper(): ActivityWrapper?
