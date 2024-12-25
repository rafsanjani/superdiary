package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object LoginScreen : AppRoute

    @Serializable
    data object RegisterScreen : AppRoute

    @Serializable
    data object RegistrationConfirmationScreen : AppRoute

    @Serializable
    data class BottomNavigationScreen(
        val userInfo: UserInfo?,
    ) : AppRoute

    @Serializable
    data class DetailScreen(val diaryId: String) : AppRoute

    @Serializable
    data object CreateDiaryScreen : AppRoute

    @Serializable
    data object DiaryListScreen : AppRoute

    @Serializable
    sealed interface BottomNavigationRoute : AppRoute {
        @Serializable
        data object DashboardTab : BottomNavigationRoute, SuperDiaryTab {
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

        @Serializable
        data object FavoriteTab : BottomNavigationRoute, SuperDiaryTab {
            override val selectedIcon: VectorPainter
                @Composable
                get() = rememberVectorPainter(Icons.Default.Favorite)

            override val options: TabOptions
                @Composable
                get() = TabOptions(
                    index = 3u,
                    title = "Favorites",
                    icon = rememberVectorPainter(Icons.Default.FavoriteBorder),
                )
        }

        @Serializable
        data object DiaryChatTab : BottomNavigationRoute, SuperDiaryTab {
            override val selectedIcon: VectorPainter
                @Composable
                get() = rememberVectorPainter(Icons.AutoMirrored.Filled.Chat)
            override val options: TabOptions
                @Composable
                get() = TabOptions(
                    index = 4u,
                    title = "Diary AI",
                    icon = rememberVectorPainter(Icons.AutoMirrored.Outlined.Chat),
                )
        }
    }
}
