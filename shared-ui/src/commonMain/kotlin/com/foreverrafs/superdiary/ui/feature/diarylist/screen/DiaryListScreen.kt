package com.foreverrafs.superdiary.ui.feature.diarylist.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import com.foreverrafs.superdiary.ui.SuperDiaryScreen
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel

object DiaryListScreen : SuperDiaryScreen {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 1u,
            title = "Entries",
            icon = rememberVectorPainter(Icons.Outlined.List),
        )

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Filled.List)

    @Composable
    override fun Content() {
        val screenModel: DiaryListViewModel = getScreenModel()
        val screenState by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.observeDiaries()
        }

        var diaryFilters by rememberSaveable(stateSaver = DiaryFilters.Saver) {
            mutableStateOf(DiaryFilters())
        }

        observeFilterEvents(diaryFilters, screenModel)

        val screenNavigator = LocalScreenNavigator.current
        val snackbarHostState = LocalRootSnackbarHostState.current

        val diaryListActions = remember {
            DiaryListActions(
                onAddEntry = {
                    screenNavigator.push(
                        CreateDiaryScreen(),
                    )
                },
                onDeleteDiaries = screenModel::deleteDiaries,
                onApplyFilters = {
                    diaryFilters = it
                },
                onToggleFavorite = screenModel::toggleFavorite,
                onDiaryClicked = {
//                    screenNavigator.push(CreateDiaryScreen(it))
                },
                onBackPressed = screenNavigator::popUntilRoot,
            )
        }

        DiaryListScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = screenState,
            showSearchBar = true,
            diaryFilters = diaryFilters,
            diaryListActions = diaryListActions,
            snackbarHostState = snackbarHostState,
        )
    }

    @Composable
    private fun observeFilterEvents(
        diaryFilters: DiaryFilters,
        screenModel: DiaryListViewModel,
    ) {
        LaunchedEffect(diaryFilters) {
            // Filter by entry only
            if (diaryFilters.entry.isNotEmpty() && diaryFilters.date == null) {
                screenModel.filterByEntry(diaryFilters.entry)
                return@LaunchedEffect
            }

            // Filter by date only
            if (diaryFilters.date != null && diaryFilters.entry.isEmpty()) {
                screenModel.filterByDate(diaryFilters.date)
                return@LaunchedEffect
            }

            // Filter by both date and entry
            if (diaryFilters.date != null && diaryFilters.entry.isNotEmpty()) {
                screenModel.filterByDateAndEntry(diaryFilters.date, diaryFilters.entry)
                return@LaunchedEffect
            }

            // No filter applied
            screenModel.observeDiaries()
        }
    }
}
