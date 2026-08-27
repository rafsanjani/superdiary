package com.foreverrafs.superdiary.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.foreverrafs.superdiary.design.icons.SuperDiaryIcon

interface SuperDiaryTab : NavKey {
    val selectedIcon: SuperDiaryIcon
    val title: String
    val icon: SuperDiaryIcon
}
