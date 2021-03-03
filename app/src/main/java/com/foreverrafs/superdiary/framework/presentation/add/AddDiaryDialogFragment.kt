package com.foreverrafs.superdiary.framework.presentation.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.databinding.BottomSheetAddDiaryBinding
import com.foreverrafs.superdiary.framework.presentation.add.state.AddDiaryState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "AddDiaryDialogFragment"

@AndroidEntryPoint
class AddDiaryDialogFragment : BottomSheetDialogFragment() {
    private val addDiaryViewModel: AddDiaryViewModel by viewModels()

    private var _binding: BottomSheetAddDiaryBinding? = null
    private val binding get() = _binding!!

    private val hints = listOf(
        "On this day, I met my damsel in distress...",
        "Today was one memorable day...",
        "Today couldn't have gone any better,...",
        "I finally accepted that dream job offer...",
        "Once upon a story"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddDiaryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpDialogBehaviour()
        setUpViewsAndClickListeners()
        loadDraftEntry()
        setUpTodayDateText()
        observeDiarySaveState()
    }

    private fun setUpViewsAndClickListeners() = with(binding) {
        dismiss.setOnClickListener {
            if (textDiaryEntry.text.isNotEmpty()) {
                showSaveDraftDialog()
                return@setOnClickListener
            }
            closeDialog()
        }

        textDiaryEntry.hint = hints.random()

        textDiaryEntry.requestFocus()

        textDiaryEntry.addTextChangedListener {
            btnDone.isEnabled = it?.isNotEmpty()!!
        }

        btnDone.setOnClickListener {
            onDoneClicked()
        }
    }

    private fun showSaveDraftDialog() {
        val diaryEntry = binding.textDiaryEntry.text.toString()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_title_save)
            .setMessage(R.string.dialog_save_draft)
            .setPositiveButton(R.string.discard) { _, _ ->
                addDiaryViewModel.clearDiaryDraft()
                closeDialog()

            }.setNegativeButton(R.string.draft) { _, _ ->
                addDiaryViewModel.saveDiaryDraft(diaryEntry)
                closeDialog()

            }
            .setNeutralButton(R.string.cancel, null)
            .show()
    }

    private fun setUpDialogBehaviour() {
        (requireDialog() as BottomSheetDialog).apply {
            isCancelable = false
            behavior.isDraggable = false
            dismissWithAnimation = true
        }
    }

    private fun loadDraftEntry() = lifecycleScope.launchWhenStarted {
        addDiaryViewModel.diaryDraftEntry.collect { draft ->

            draft?.let {
                binding.textDiaryEntry.setText(it)
                binding.textDiaryEntry.selectAll()
            }
        }
    }

    private fun closeDialog() {
        binding.dismiss.animate()
            .rotation(180f)
            .setDuration(400L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                dismiss()
            }
    }

    private fun onDoneClicked() {
        val entry = binding.textDiaryEntry.text.toString()
        saveDiary(entry)
    }

    private fun saveDiary(entry: String) {
        val diary = Diary(
            message = entry
        )

        addDiaryViewModel.saveDiary(diary)
    }

    private fun setUpTodayDateText() {
        val formatter = DateTimeFormatter.ofPattern("EEE MMM dd, yyyy")
        binding.tvTodayDate.text = formatter.format(LocalDate.now())
    }

    private fun observeDiarySaveState() {
        lifecycleScope.launchWhenStarted {
            addDiaryViewModel.viewState.collect { state ->
                if (state == null) return@collect

                when (state) {
                    is AddDiaryState.Saved -> renderSavedState(state.diary)
                    AddDiaryState.Saving -> renderSavingState()
                    is AddDiaryState.Error -> renderErrorState(state.error)
                }
            }
        }
    }

    private fun renderSavingState() {
        Log.d(TAG, "renderSavingState: Saving Diary")
        Snackbar.make(requireView(), "Saving Diary", Snackbar.LENGTH_SHORT).show()
    }

    private fun renderSavedState(diary: Diary) {
        Log.d(TAG, "renderSavingState: Diary Saved Successfully! $diary")

        closeDialog()
    }

    private fun renderErrorState(error: Throwable) {
        Log.e(TAG, "renderErrorState: Error saving diary ", error)
        Toast.makeText(requireContext(), "Error saving diary", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}