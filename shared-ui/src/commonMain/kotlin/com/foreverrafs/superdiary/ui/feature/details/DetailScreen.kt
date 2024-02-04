package com.foreverrafs.superdiary.ui.feature.details

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.ui.LocalScreenNavigator

class DetailScreen(val diary: Diary) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalScreenNavigator.current
        val screenModel: DetailsViewModel = getScreenModel()

        DetailScreenContent(
            onNavigateBack = navigator::pop,
            diary = diary,
            onDeleteDiary = {
                screenModel.deleteDiary(diary)
            },
        )
    }
}
