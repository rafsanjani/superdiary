package com.foreverrafs.superdiary.design.components

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(onBack: () -> Unit)
