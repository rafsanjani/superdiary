package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import kotlinx.serialization.Serializable

@Serializable
sealed interface TopLevelRoute : SuperDiaryTab {
    @Serializable
    data object DashboardTab : TopLevelRoute {
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
    data object FavoriteTab : TopLevelRoute {
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
    data object DiaryChatTab : TopLevelRoute {
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

    companion object Companion {
        val Items: List<TopLevelRoute> = listOf(DashboardTab, FavoriteTab, DiaryChatTab)

        val Saver: Saver<TopLevelRoute, String> = Saver(
            save = { it::class.qualifiedName },
            restore = { qualifiedClass ->
                Items.firstOrNull { it::class.qualifiedName == qualifiedClass } ?: DashboardTab
            },
        )
    }
}
