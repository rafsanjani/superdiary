package com.foreverrafs.superdiary.ui.feature.creatediary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.ui.components.NavigationIcon
import com.foreverrafs.superdiary.ui.components.SaveIcon
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
    var entry by remember {
        mutableStateOf("")
    }
    Scaffold(
        topBar = {
            SuperDiaryAppBar(
                navigationIcon = {
                    NavigationIcon {
                        navigator.pop()
                    }
                },
                saveIcon = {
                    if (entry.isNotEmpty()) {
                        SaveIcon {
                        }
                    }
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Story",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                value = entry,
                onValueChange = {
                    entry = it
                },
                placeholder = {
                    Text(
                        text = "Write something...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

class CreateDiaryScreenModel(
    private val addDiaryUseCase: AddDiaryUseCase,
) : ScreenModel {
    fun saveDiary(diary: Diary) = coroutineScope.launch {
        addDiaryUseCase(diary)
    }
}
