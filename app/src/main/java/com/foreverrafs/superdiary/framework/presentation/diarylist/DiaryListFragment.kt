package com.foreverrafs.superdiary.framework.presentation.diarylist

import SuperDiaryTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.foreverrafs.datepicker.DatePickerTimeline
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.framework.presentation.diarylist.state.DiaryListState
import com.foreverrafs.superdiary.framework.presentation.style.colorPrimary
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

private const val TAG = "DiaryListFragment"

@ExperimentalComposeUiApi
@AndroidEntryPoint
class DiaryListFragment : Fragment() {
    private val diaryListViewModel: DiaryListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val viewState by diaryListViewModel.viewState.collectAsState(initial = DiaryListState.Loading)

                viewState?.let {
                    DiaryList(
                        viewState = it,
                        onDiaryDeleted = {

                        },
                        onDateSelected = {

                        }
                    )
                }
            }
        }
    }

    @ExperimentalComposeUiApi
    @Composable
    fun DiaryList(
        viewState: DiaryListState,
        onDiaryDeleted: (diary: Diary) -> Unit,
        onDateSelected: (date: LocalDate) -> Unit
    ) {
        SuperDiaryTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = MaterialTheme.colors.primary
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "SuperDiary", modifier = Modifier
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h4
                            )
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .align(Alignment.CenterVertically)

                            )
                        }

                        DatePickerTimeline(
                            onDateSelected = {},
                            backgroundColor = colorPrimary,
                            dateTextColor = Color.White,
                            selectedTextColor = Color.Black.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }
    }

    @ExperimentalComposeUiApi
    @Composable
    @Preview
    fun Preview() {
        DiaryList(
            viewState = DiaryListState.Loading,
            onDiaryDeleted = {},
            onDateSelected = {}
        )
    }
}

