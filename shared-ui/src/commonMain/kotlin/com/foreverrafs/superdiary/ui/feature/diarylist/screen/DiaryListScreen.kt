package com.foreverrafs.superdiary.ui.feature.diarylist.screen

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
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import org.koin.compose.koinInject

@Composable
fun DiaryListScreen(
    avatarUrl: String?,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val screenModel: DiaryListViewModel = koinInject()
    val screenState by screenModel.state.collectAsState()

    var diaryFilters by rememberSaveable(stateSaver = DiaryFilters.Saver) {
        mutableStateOf(DiaryFilters())
    }

    LaunchedEffect(diaryFilters) {
        screenModel.applyFilter(diaryFilters)
    }

    val diaryListActions = remember {
        DiaryListActions(
            onAddEntry = {
                navController.navigate(AppRoute.CreateDiaryScreen)
            },
            onDeleteDiaries = screenModel::deleteDiaries,
            onApplyFilters = {
                diaryFilters = it
            },
            onToggleFavorite = screenModel::toggleFavorite,
            onDiaryClicked = {
                navController.navigate(AppRoute.DetailScreen(it.toString()))
            },
            onBackPressed = {
                navController.popBackStack()
            },
        )
    }

    DiaryListScreenContent(
        modifier = modifier.fillMaxSize(),
        state = screenState,
        showSearchBar = true,
        diaryFilters = diaryFilters,
        diaryListActions = diaryListActions,
        avatarUrl = avatarUrl,
    )
}
