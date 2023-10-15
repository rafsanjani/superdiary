package com.foreverrafs.superdiary.ui.feature.creatediary

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CreateDiaryScreen : Screen, KoinComponent {
    private val createDiaryPageModel: CreateDiaryPageModel by inject()

    @Composable
    override fun Content() {
        TextField(
            value = createDiaryPageModel.text,
            onValueChange = {
                createDiaryPageModel.text = it
            },
        )
    }
}

class CreateDiaryPageModel : ScreenModel {
    var text by mutableStateOf("")
}
