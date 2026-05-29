package com.foreverrafs.superdiary.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface TopLevelRoute : SuperDiaryTab {
    @Serializable
    data object DashboardTab : TopLevelRoute {
        override val selectedIcon: ImageVector = Icons.Filled.StackedBarChart

        override val title: String = "Dashboard"
        override val icon: ImageVector = Icons.Outlined.BarChart
    }

    @Serializable
    data object DiaryList : TopLevelRoute {
        override val selectedIcon: ImageVector = Icons.AutoMirrored.Filled.List

        override val title: String = "List"
        override val icon: ImageVector = Icons.AutoMirrored.Outlined.List
    }

    @Serializable
    data object FavoriteTab : TopLevelRoute {
        override val selectedIcon: ImageVector = Icons.Default.Favorite
        override val icon: ImageVector = Icons.Default.FavoriteBorder

        override val title: String = "Favorites"
    }

    @Serializable
    data object DiaryChatTab : TopLevelRoute {
        override val selectedIcon: ImageVector = Icons.AutoMirrored.Filled.Chat
        override val icon: ImageVector = Icons.AutoMirrored.Outlined.Chat
        override val title: String = "Diary AI"
    }

    companion object Companion {
        val Items: Set<TopLevelRoute> = setOf(DashboardTab, DiaryList, FavoriteTab, DiaryChatTab)

        val SavedStateConfiguration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(baseClass = NavKey::class) {
                    subclass(subclass = DashboardTab::class, DashboardTab.serializer())
                    subclass(subclass = FavoriteTab::class, FavoriteTab.serializer())
                    subclass(subclass = DiaryChatTab::class, DiaryChatTab.serializer())
                    subclass(subclass = DiaryList::class, DiaryList.serializer())
                }
            }
        }
    }
}
