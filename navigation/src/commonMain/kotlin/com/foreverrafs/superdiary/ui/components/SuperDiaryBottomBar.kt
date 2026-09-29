package com.foreverrafs.superdiary.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab

@Composable
expect fun SuperDiaryBottomBar(
    items: List<SuperDiaryTab>,
    selected: NavKey,
    onItemClick: (SuperDiaryTab) -> Unit,
    modifier: Modifier = Modifier,
)
