package com.foreverrafs.superdiary.ui.feature.dashboard

// import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.data.utils.DiarySettings
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreen
import com.foreverrafs.superdiary.ui.feature.details.DetailScreen
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreen
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab
import com.foreverrafs.superdiary.ui.navigation.TabOptions
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
object DashboardTab : SuperDiaryTab {
    @Composable
    fun Content(
        navController: NavHostController,
        snackbarHostState: SnackbarHostState,
    ) {
        val screenModel: DashboardViewModel = koinInject()
        val screenState by screenModel.state.collectAsState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            screenModel.loadDashboardContent()
        }

        val settings by screenModel.settings.collectAsState(initial = DiarySettings.Empty)

        DashboardScreenContent(
            state = screenState,
            onAddEntry = {
                navController.navigate(CreateDiaryScreen)
            },
            onSeeAll = {
                navController.navigate(DiaryListScreen)
            },
            onToggleFavorite = {
                coroutineScope.launch {
                    if (screenModel.toggleFavorite(it)) {
                        snackbarHostState.showSnackbar("Favorite Updated")
                    }
                }
            },
            settings = settings,
            onChangeSettings = screenModel::updateSettings,
            onDiaryClick = { diary ->
                diary.id?.let {
                    navController.navigate(
                        DetailScreen(it.toString()),
                    )
                }
            },
        )
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Filled.StackedBarChart)

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Dashboard",
            icon = rememberVectorPainter(Icons.Outlined.BarChart),
        )
}
