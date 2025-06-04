@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.foreverrafs.superdiary.list.presentation.screen.list

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.foreverrafs.superdiary.list.DiaryFilters
import com.foreverrafs.superdiary.list.DiaryListActions
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiaryListScreen(
    navController: NavController,
    onAddEntry: () -> Unit,
    onDiaryClick: (id: Long) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenModel: DiaryListViewModel = koinViewModel()
    val screenState by screenModel.state.collectAsState()

    var diaryFilters by rememberSaveable(stateSaver = DiaryFilters.Companion.Saver) {
        mutableStateOf(DiaryFilters())
    }

    LaunchedEffect(diaryFilters) {
        screenModel.applyFilter(diaryFilters)
    }

    val diaryListActions = remember {
        DiaryListActions(
            onAddEntry = onAddEntry,
            onDeleteDiaries = screenModel::deleteDiaries,
            onApplyFilters = {
                diaryFilters = it
            },
            onToggleFavorite = screenModel::toggleFavorite,
            onDiaryClicked = onDiaryClick,
            onBackPressed = navController::navigateUp,
        )
    }

    DiaryListScreenContent(
        modifier = modifier.fillMaxSize(),
        state = screenState,
        showSearchBar = true,
        diaryFilters = diaryFilters,
        diaryListActions = diaryListActions,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onProfileClick = onProfileClick,
    )
}
