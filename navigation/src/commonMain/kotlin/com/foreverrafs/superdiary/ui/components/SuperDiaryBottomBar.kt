package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation3.runtime.NavKey
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab

@Composable
fun SuperDiaryBottomBar(
    items: List<SuperDiaryTab>,
    selected: NavKey,
    onItemClick: (SuperDiaryTab) -> Unit,
) {
    NavigationBar {
        items.forEach { tab ->
            BottomNavigationItem(
                tab = tab,
                selected = selected == tab,
            ) {
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
        modifier = Modifier.testTag(tab.title),
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = if (selected) {
                    rememberVectorPainter(tab.selectedIcon)
                } else {
                    rememberVectorPainter(tab.icon)
                },
                contentDescription = tab.title,
            )
        },
        label = {
            Text(
                text = tab.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        },
    )
}
