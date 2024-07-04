package com.foreverrafs.superdiary.ui.home

// import com.foreverrafs.superdiary.ui.LocalRootSnackbarHostState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardTab
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatTab
import com.foreverrafs.superdiary.ui.feature.favorites.screen.FavoriteTab
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab
import kotlinx.serialization.Serializable

/**
 * Provides a navigation entry point for all the screens that rely on
 * bottom tab for navigation
 */

@Serializable
object BottomNavigationScreen {

    @Composable
    fun Content(
        rootNavController: NavHostController,
        modifier: Modifier = Modifier,
    ) {
        val tabNavController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            topBar = { SuperDiaryAppBar() },
            bottomBar = {
                SuperDiaryBottomBar(tabNavController)
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background,
                content = {
                    NavHost(
                        navController = tabNavController,
                        startDestination = DashboardTab,
                    ) {
                        composable<DashboardTab> {
                            DashboardTab.Content(rootNavController)
                        }

                        composable<FavoriteTab> {
                            FavoriteTab.Content(
                                snackbarHostState = snackbarHostState,
                                navController = rootNavController,
                            )
                        }

                        composable<DiaryChatTab> {
                            DiaryChatTab.Content()
                        }
                    }
                },
            )
        }
    }

    @Composable
    private fun SuperDiaryBottomBar(navController: NavController) {
        val items = listOf(DashboardTab, FavoriteTab, DiaryChatTab)
        var selectedItem by remember { mutableStateOf<SuperDiaryTab>(DashboardTab) }

        NavigationBar {
            items.forEach { tab ->
                BottomNavigationItem(
                    tab = tab,
                    selected = selectedItem == tab,
                ) {
                    selectedItem = tab
                    navController.navigate(tab) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.BottomNavigationItem(
        tab: SuperDiaryTab,
        selected: Boolean,
        onClick: () -> Unit,
    ) {
        NavigationBarItem(
            modifier = Modifier.testTag(tab.options.title),
            selected = selected,
            onClick = onClick,
            icon = {
                Icon(
                    painter = if (selected) {
                        tab.selectedIcon
                    } else {
                        tab.options.icon
                            ?: rememberVectorPainter(Icons.Default.Home)
                    },
                    contentDescription = tab.options.title,
                )
            },
            label = {
                Text(
                    text = tab.options.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                )
            },
        )
    }
}
