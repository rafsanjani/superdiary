package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab

@Composable
fun SuperDiaryBottomBar(
    items: List<SuperDiaryTab>,
    onItemClick: (SuperDiaryTab) -> Unit,
) {
    NavigationBar {
        var selectedTab by remember {
            mutableStateOf(
                items.firstOrNull() ?: throw IllegalArgumentException("You should add at least one tab"),
            )
        }

        items.forEach { tab ->
            val selected = selectedTab == tab

            BottomNavigationItem(
                tab = tab,
                selected = selected,
            ) {
                selectedTab = tab
                onItemClick(tab)
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
