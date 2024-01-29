package com.foreverrafs.superdiary.ui.feature.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.LocalScreenNavigator

class DetailScreen(val diary: Diary) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalScreenNavigator.current

        DetailScreenContent(
            onNavigateBack = navigator::pop,
            diary = diary,
        )
    }
}
