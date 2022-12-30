package com.foreverrafs.superdiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.foreverrafs.superdiary.Database
import com.foreverrafs.superdiary.DatabaseDriverFactory
import com.foreverrafs.superdiary.FlowLocalDataSource
import com.foreverrafs.superdiary.diary.datasource.DataSource
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.diary.usecase.DeleteAllDiariesUseCase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataSource: DataSource = FlowLocalDataSource(Database(DatabaseDriverFactory(this)))

        val clearDiaries = DeleteAllDiariesUseCase(dataSource)
        val add = AddDiaryUseCase(dataSource)
        val fetchAll = dataSource.fetchAllAsFlow()

        lifecycleScope.launch {
            clearDiaries()
        }

        setContent {
            var scope = rememberCoroutineScope()

            val items by fetchAll.collectAsState(initial = listOf())

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        var entry by remember {
                            mutableStateOf("")
                        }

                        TextField(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            value = entry,
                            onValueChange = {
                                entry = it
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
                            onClick = {
                                scope.launch {
                                    add(
                                        Diary(
                                            null,
                                            entry,
                                            "Today's Date"
                                        )
                                    )
                                }
                            }
                        ) {
                            Text("Add Diary")
                        }

                        LazyColumn {
                            items(items) {
                                Text(it.entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
