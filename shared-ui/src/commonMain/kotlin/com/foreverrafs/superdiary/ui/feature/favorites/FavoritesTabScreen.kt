package com.foreverrafs.superdiary.ui.feature.favorites

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryList

@Composable
fun FavoritesTabScreen(state: FavoritesTabScreenState) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SuperDiaryAppBar()
        },
    ) {
        when (state) {
            is FavoritesTabScreenState.Favorites -> {
                DiaryList(
                    modifier = Modifier.fillMaxSize().padding(it),
                    diaries = state.diaries,
                    onToggleFavorite = {},
                    onCancelSelection = {},
                    onAddSelection = {},
                    onRemoveSelection = {},
                    onDeleteDiaries = {},
                    onApplyFilters = {},
                    onAddEntry = {},
                    onToggleSelection = {},
                    diaryFilters = DiaryFilters(),
                    showSearchBar = false,
                    selectedIds = setOf(),
                    inSelectionMode = false,
                )
            }

            is FavoritesTabScreenState.Idle -> {}
        }
    }
}
