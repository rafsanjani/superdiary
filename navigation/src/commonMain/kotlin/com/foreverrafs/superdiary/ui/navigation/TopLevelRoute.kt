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
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

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
        val Items: Set<TopLevelRoute> = setOf(DashboardTab, FavoriteTab, DiaryChatTab)

        val SavedStateConfiguration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(baseClass = NavKey::class) {
                    subclass(subclass = DashboardTab::class, DashboardTab.serializer())
                    subclass(subclass = FavoriteTab::class, FavoriteTab.serializer())
                    subclass(subclass = DiaryChatTab::class, DiaryChatTab.serializer())
                }
            }
        }
    }
}
