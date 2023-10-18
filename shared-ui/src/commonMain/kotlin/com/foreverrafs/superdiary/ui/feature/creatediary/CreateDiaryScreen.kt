package com.foreverrafs.superdiary.ui.feature.creatediary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import kotlinx.coroutines.launch

object CreateDiaryScreen : Screen {

    @Composable
    override fun Content() {
        val createDiaryScreenModel: CreateDiaryScreenModel = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        CreateDiaryScreenContent(navigator)
    }
}

@Composable
private fun CreateDiaryScreenContent(navigator: Navigator) {
    Scaffold(
        topBar = {
            SuperDiaryAppBar(
                showBackIcon = true,
                onBackClicked = navigator::pop,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
    }
}

class CreateDiaryScreenModel(
    private val addDiaryUseCase: AddDiaryUseCase,
) : ScreenModel {
    fun saveDiary(diary: Diary) = coroutineScope.launch {
        addDiaryUseCase(diary)
    }
}
