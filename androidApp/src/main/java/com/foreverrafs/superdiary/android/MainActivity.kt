package com.foreverrafs.superdiary.android

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.ui.App
import com.foreverrafs.superdiary.ui.AppTheme
import com.foreverrafs.superdiary.ui.components.DiaryList
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            enableEdgeToEdge()
            App()
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun DiaryListPreview() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryList(
                diaries = listOf(
                    Diary(
                        id = Random.nextLong(),
                        entry = "Hello barbashe",
                        date = "2023-01-01",
                    ),

                    Diary(
                        id = Random.nextLong(),
                        entry = "Hello barbashe",
                        date = "2023-02-02",
                    ),
                ),
            )
        }
    }
}
