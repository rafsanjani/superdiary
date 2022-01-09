package com.foreverrafs.superdiary.ui.feature_diary.add

import SuperDiaryTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.superdiary.ui.style.brandColorDark
import kotlinx.coroutines.launch

private const val TAG = "AddDiaryDialogFragment"

@Composable
fun AddDiaryScreen(navController: NavHostController) {
    val addDiaryViewModel: AddDiaryViewModel = hiltViewModel()
    val addDiaryState by addDiaryViewModel.viewState.collectAsState()

    AddDiaryScreen(
        addDiaryState = addDiaryState,
        onViewEvent = {
            addDiaryViewModel.onEvent(it)
        },
        onDiarySaved = {
            println("Diary saved")
        }
    )
}

@Composable
fun AddDiaryScreen(
    addDiaryState: AddDiaryState?,
    onViewEvent: (AddDiaryEvent) -> Unit,
    onDiarySaved: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val hints = remember {
        listOf(
            "On this day, I met my damsel in distress...",
            "Today was one memorable day...",
            "Today couldn't have gone any better,...",
            "I finally accepted that dream job offer...",
            "Once upon a story"
        )
    }

    val hint = remember {
        hints.random()
    }

    LaunchedEffect(addDiaryState) {
        addDiaryState?.let {
            when (it) {
                is AddDiaryState.Error -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Error Saving Diary Entry",
                        )
                    }
                }

                is AddDiaryState.Success -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Successfully Saved Entry",
                        )

                        onDiarySaved()
                    }
                }
            }
        }
    }


    SnackbarDefaults.backgroundColor
    Scaffold(
        scaffoldState = scaffoldState,

        ) {
        SuperDiaryTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .background(color = brandColorDark)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)

                ) {
                    Header(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "Title",
                        style = MaterialTheme.typography.h5
                    )

                    var diaryTitle by remember {
                        mutableStateOf("")
                    }
                    var diaryMessage by remember {
                        mutableStateOf("")
                    }

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = diaryTitle,
                        onValueChange = { diaryTitle = it },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        placeholder = {
                            Text(text = "Entry #1, the happy day")
                        },
                        singleLine = true,
                    )


                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "Whats on your mind?",
                        style = MaterialTheme.typography.h5
                    )

                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        value = diaryMessage,
                        onValueChange = { diaryMessage = it },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                        ),
                        placeholder = {
                            Text(text = hint)
                        },
                        singleLine = false,
                    )

                    Button(
                        modifier = Modifier
                            .height(64.dp)
                            .width(120.dp)
                            .padding(bottom = 16.dp)
                            .align(Alignment.End),
                        onClick = {
                            onViewEvent(
                                AddDiaryEvent.SaveDiary(
                                    diary = Diary(
                                        title = diaryTitle,
                                        message = diaryMessage,
                                    )
                                )
                            )
                        },
                        shape = CircleShape
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Write", modifier = Modifier
                .padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h4
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Preview
@Composable
fun Preview() {

}

//@AndroidEntryPoint
//class AddDiaryDialogFragment : BottomSheetDialogFragment() {
//    private val addDiaryViewModel: AddDiaryViewModel by viewModels()
//
//    private var _binding: BottomSheetAddDiaryBinding? = null
//    private val binding get() = _binding!!
//
//    private val hints = listOf(
//        "On this day, I met my damsel in distress...",
//        "Today was one memorable day...",
//        "Today couldn't have gone any better,...",
//        "I finally accepted that dream job offer...",
//        "Once upon a story"
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = BottomSheetAddDiaryBinding.inflate(inflater, container, false)
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        setUpDialogBehaviour()
//        setUpViewsAndClickListeners()
//        loadDraftEntry()
//        setUpTodayDateText()
//        observeDiarySaveState()
//    }
//
//    private fun setUpViewsAndClickListeners() = with(binding) {
//        dismiss.setOnClickListener {
//            if (textDiaryEntry.text.isNotEmpty()) {
//                showSaveDraftDialog()
//                return@setOnClickListener
//            }
//            closeDialog()
//        }
//
//        textDiaryEntry.hint = hints.random()
//
//        textDiaryEntry.requestFocus()
//
//        textDiaryEntry.addTextChangedListener {
//            btnDone.isEnabled = it?.isNotEmpty()!!
//        }
//
//        btnDone.setOnClickListener {
//            onDoneClicked()
//        }
//    }
//
//    private fun showSaveDraftDialog() {
//        val diaryEntry = binding.textDiaryEntry.text.toString()
//
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle(R.string.dialog_title_save)
//            .setMessage(R.string.dialog_save_draft)
//            .setPositiveButton(R.string.discard) { _, _ ->
//                addDiaryViewModel.clearDiaryDraft()
//                closeDialog()
//
//            }.setNegativeButton(R.string.draft) { _, _ ->
//                addDiaryViewModel.saveDiaryDraft(diaryEntry)
//                closeDialog()
//            }
//            .setNeutralButton(R.string.cancel, null)
//            .show()
//    }
//
//    private fun setUpDialogBehaviour() {
//        (requireDialog() as BottomSheetDialog).apply {
//            isCancelable = false
//            behavior.isDraggable = false
//            dismissWithAnimation = true
//        }
//    }
//
//    private fun loadDraftEntry() = lifecycleScope.launchWhenStarted {
//        addDiaryViewModel.diaryDraftEntry.collect { draft ->
//
//            draft?.let {
//                binding.textDiaryEntry.setText(it)
//                binding.textDiaryEntry.selectAll()
//            }
//        }
//    }
//
//    private fun closeDialog() {
//        binding.dismiss.animate()
//            .rotation(180f)
//            .setDuration(400L)
//            .setInterpolator(AccelerateDecelerateInterpolator())
//            .withEndAction {
//                dismiss()
//            }
//    }
//
//    private fun onDoneClicked() {
////        val entry = binding.textDiaryEntry.text.toString()
////        saveDiary(entry)
//    }
//
//    private fun saveDiary(entry: String, title: String) {
//        val diary = Diary(
//            message = entry,
//            title = title
//        )
//
//        addDiaryViewModel.saveDiary(diary)
//    }
//
//    private fun setUpTodayDateText() {
//        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy")
//        binding.tvTodayDate.text = formatter.format(LocalDate.now())
//    }
//
//    private fun observeDiarySaveState() {
//        lifecycleScope.launchWhenStarted {
//            addDiaryViewModel.viewState.collect { state ->
//                if (state == null) return@collect
//
//                when (state) {
//                    is AddDiaryState.Saved -> renderSavedState(state.diary)
//                    AddDiaryState.Saving -> renderSavingState()
//                    is AddDiaryState.Error -> renderErrorState(state.error)
//                }
//            }
//        }
//    }
//
//    private fun renderSavingState() {
//        Log.d(TAG, "renderSavingState: Saving Diary")
//        Snackbar.make(requireView(), "Saving Diary", Snackbar.LENGTH_SHORT).show()
//    }
//
//    private fun renderSavedState(diary: Diary) {
//        Log.d(TAG, "renderSavingState: Diary Saved Successfully! $diary")
//
//        closeDialog()
//    }
//
//    private fun renderErrorState(error: Throwable) {
//        Log.e(TAG, "renderErrorState: Error saving diary ", error)
//        Toast.makeText(requireContext(), "Error saving diary", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }
//}