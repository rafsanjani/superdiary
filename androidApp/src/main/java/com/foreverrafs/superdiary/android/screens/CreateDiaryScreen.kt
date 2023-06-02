package com.foreverrafs.superdiary.android.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

typealias CreateDiaryScreen = @Composable () -> Unit

@Composable
@Inject
fun CreateDiaryScreen(addDiaryUseCase: AddDiaryUseCase) {
    val coroutineScope = rememberCoroutineScope()

    Content { diary ->
        coroutineScope.launch {
            addDiaryUseCase(diary)
        }
    }
}

@Composable
@Suppress("UnusedPrivateMember")
private fun Content(onAddDiary: (diary: Diary) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val today = LocalDateTime.now()
        Text(
            text = "Entry for today: ${DateTimeFormatter.ofPattern("dd MMM, yyyy").format(today)}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )

        var entry by remember {
            mutableStateOf("")
        }

        OutlinedTextField(
            value = entry,
            onValueChange = { entry = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        )
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(2.dp)
        ) {
            Text(text = "Save", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            Content(onAddDiary = {})
        }
    }
}
