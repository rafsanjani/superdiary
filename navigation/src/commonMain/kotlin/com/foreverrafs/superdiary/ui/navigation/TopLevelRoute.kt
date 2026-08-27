package com.foreverrafs.superdiary.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.foreverrafs.superdiary.design.icons.SuperDiaryIcon
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
sealed interface TopLevelRoute : SuperDiaryTab {
    @Serializable
    data object DashboardTab : TopLevelRoute {
        override val selectedIcon: SuperDiaryIcon = SuperDiaryIcon.StackedBarChart

        override val title: String = "Dashboard"
        override val icon: SuperDiaryIcon = SuperDiaryIcon.BarChart
    }

    @Serializable
    data object DiaryList : TopLevelRoute {
        override val selectedIcon: SuperDiaryIcon = SuperDiaryIcon.List

        override val title: String = "List"
        override val icon: SuperDiaryIcon = SuperDiaryIcon.List
    }

    @Serializable
    data object FavoriteTab : TopLevelRoute {
        override val selectedIcon: SuperDiaryIcon = SuperDiaryIcon.Favorite
        override val icon: SuperDiaryIcon = SuperDiaryIcon.FavoriteBorder

        override val title: String = "Favorites"
    }

    @Serializable
    data object WritingInsightsTab : TopLevelRoute {
        override val selectedIcon: SuperDiaryIcon = SuperDiaryIcon.Lightbulb
        override val icon: SuperDiaryIcon = SuperDiaryIcon.LightbulbOutline
        override val title: String = "Insights"
    }

    companion object Companion {
        val Items: Set<TopLevelRoute> =
            setOf(DashboardTab, DiaryList, FavoriteTab, WritingInsightsTab)

        val SavedStateConfiguration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(baseClass = NavKey::class) {
                    subclass(subclass = DashboardTab::class, DashboardTab.serializer())
                    subclass(subclass = FavoriteTab::class, FavoriteTab.serializer())
                    subclass(
                        subclass = WritingInsightsTab::class,
                        WritingInsightsTab.serializer(),
                    )
                    subclass(subclass = DiaryList::class, DiaryList.serializer())
                }
            }
        }
    }
}
