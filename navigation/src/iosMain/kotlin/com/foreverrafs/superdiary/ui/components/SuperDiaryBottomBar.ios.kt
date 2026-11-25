package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import androidx.navigation3.runtime.NavKey
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab
import com.foreverrafs.superdiary.ui.navigation.TopLevelRoute
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIImage
import platform.UIKit.UITabBar
import platform.UIKit.UITabBarDelegateProtocol
import platform.UIKit.UITabBarItem

private const val BOTTOM_BAR_TEST_TAG = "native-ios-liquid-glass-bottom-bar"

@Composable
actual fun SuperDiaryBottomBar(
    items: List<SuperDiaryTab>,
    selected: NavKey,
    onItemClick: (SuperDiaryTab) -> Unit,
    modifier: Modifier,
) {
    UIKitView(
        factory = { LiquidGlassTabBar() },
        modifier = modifier
            .height(84.dp)
            .testTag(BOTTOM_BAR_TEST_TAG),
        update = { tabBar ->
            tabBar.update(
                items = items,
                selected = selected,
                onItemClick = onItemClick,
            )
        },
    )
}

@OptIn(ExperimentalForeignApi::class)
private class LiquidGlassTabBar : UITabBar(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)), UITabBarDelegateProtocol {
    private var currentItems: List<SuperDiaryTab> = emptyList()
    private var onItemClick: (SuperDiaryTab) -> Unit = {}

    init {
        delegate = this
    }

    fun update(
        items: List<SuperDiaryTab>,
        selected: NavKey,
        onItemClick: (SuperDiaryTab) -> Unit,
    ) {
        this.currentItems = items
        this.onItemClick = onItemClick

        val existingItems = this.items.orEmpty()
        if (existingItems.size != items.size || existingItems.zip(items).any { (tabBarItem, tab) ->
                (tabBarItem as UITabBarItem).title != tab.title
            }
        ) {
            this.items = items.mapIndexed { index, tab ->
                UITabBarItem(
                    title = tab.title,
                    image = UIImage.systemImageNamed(tab.systemImageName),
                    tag = index.toLong(),
                ).apply {
                    selectedImage = UIImage.systemImageNamed(tab.selectedSystemImageName)
                }
            }
        }

        selectedItem = this.items
            ?.getOrNull(items.indexOfFirst { it == selected }) as? UITabBarItem
    }

    override fun tabBar(tabBar: UITabBar, didSelectItem: UITabBarItem) {
        currentItems.getOrNull(didSelectItem.tag.toInt())?.let(onItemClick)
    }
}

private val SuperDiaryTab.systemImageName: String
    get() = when (this) {
        TopLevelRoute.DashboardTab -> "chart.bar"
        TopLevelRoute.DiaryList -> "list.bullet"
        TopLevelRoute.FavoriteTab -> "heart"
        TopLevelRoute.WritingInsightsTab -> "lightbulb"
        else -> "circle"
    }

private val SuperDiaryTab.selectedSystemImageName: String
    get() = when (this) {
        TopLevelRoute.DashboardTab -> "chart.bar.fill"
        TopLevelRoute.DiaryList -> "list.bullet"
        TopLevelRoute.FavoriteTab -> "heart.fill"
        TopLevelRoute.WritingInsightsTab -> "lightbulb.fill"
        else -> "circle.fill"
    }
