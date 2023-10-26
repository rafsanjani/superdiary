package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.LocalScreenNavigator
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

object CreateDiaryScreen : Screen {

    @Composable
    override fun Content() {
        val createDiaryScreenModel: CreateDiaryScreenModel = getScreenModel()
        val navigator = LocalScreenNavigator.current

        CreateDiaryScreenContent(
            onNavigateBack = navigator::pop,
        ) { entry ->
            createDiaryScreenModel.saveDiary(
                Diary(
                    entry = entry,
                    date = Clock.System
                        .now(),
                    isFavorite = false,
                ),
            )

            navigator.pop()
        }
    }
}

class CreateDiaryScreenModel(
    private val addDiaryUseCase: AddDiaryUseCase,
) : ScreenModel {

    fun saveDiary(diary: Diary) = screenModelScope.launch {
        addDiaryUseCase(diary)
    }
}
