package com.foreverrafs.superdiary.framework.presentation.diarylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.databinding.FragmentDiaryListBinding
import com.foreverrafs.superdiary.framework.presentation.common.BaseFragment
import com.foreverrafs.superdiary.framework.presentation.diarylist.state.DiaryListState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import java.time.LocalDate

private const val TAG = "DiaryListFragment"

@AndroidEntryPoint
class DiaryListFragment : BaseFragment<FragmentDiaryListBinding>() {
    private val diaryListViewModel: DiaryListViewModel by viewModels()
    private val diaryListAdapter = DiaryListAdapter(onDelete = ::onDiaryDeleted)
    private var diaryList = mutableListOf<Diary>()
    private val today = LocalDate.now()
    private var selectedDay: LocalDate = today

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDiaryListBinding {
        return FragmentDiaryListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleText.setOnClickListener {
            diaryCalendarView.smoothScrollToToday()
        }

        diaryCalendarView.addOnDateSelectedListener { selectedDay ->
            this@DiaryListFragment.selectedDay = selectedDay

            val selectedDayEntries = diaryListViewModel.filterDiariesByDate(diaryList, selectedDay)

            renderDiaryListState(selectedDayEntries)

            if (selectedDay == today)
                btnNewEntry.show()
            else
                btnNewEntry.hide()
        }

        diaryCalendarView.addOnMonthChangedListener {
            // TODO: 28/12/20
        }

        btnNewEntry.setOnClickListener {
            navController.navigate(
                DiaryListFragmentDirections.actionDiaryListFragmentToAddDiaryDialogFragment()
            )
        }

        setupDiaryList()
        observeDiaryList()
    }


    private fun onDiaryDeleted(diary: Diary) {
        diaryListAdapter.submitList(
            diaryListViewModel.filterDiariesByDate(
                diaryList.toMutableList().also {
                    it.remove(diary)
                }, today
            )
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Diary Entry")
            .setMessage("Are you sure you want to delete this diary entry?")
            .setPositiveButton(
                R.string.alert_delete
            ) { _, _ ->
                diaryListViewModel.deleteDiary(diary)
            }.setNegativeButton(android.R.string.cancel) { _, _ ->
                diaryListAdapter.submitList(
                    diaryListViewModel.filterDiariesByDate(diaryList, today)
                )
            }
            .show()
    }

    private fun setupDiaryList() = with(binding) {
        diaryListView.adapter = diaryListAdapter

        diaryListView.itemAnimator = FadeInDownAnimator()

        diaryListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy - dx > 0) {
                    binding.btnNewEntry.hide()
                } else {
                    if (selectedDay == today)
                        binding.btnNewEntry.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> binding.btnNewEntry.show()
                }
            }
        })
    }

    private fun observeDiaryList() {
        diaryListViewModel.viewState.observe(viewLifecycleOwner) { state ->
            if (state == null) return@observe

            when (state) {
                is DiaryListState.DiaryList -> {
                    diaryList = state.list.toMutableList()
                    val todayEntries = diaryListViewModel.filterDiariesByDate(diaryList, today)

                    renderDiaryListState(
                        todayEntries
                    )

                    populateCalendarWithDiaryEntryDates(diaryList)
                }
                is DiaryListState.Error -> renderErrorFetchingDiaryListState(state.error)
                DiaryListState.Loading -> renderLoadingState()
                is DiaryListState.Deleted -> {
                }
            }
        }
    }


    private fun renderDiaryListState(diaries: List<Diary>, scrollToTop: Boolean = true) {
        Log.d(TAG, "renderDiaryListState: ${diaries.size} items")
        diaryListAdapter.submitList(diaries) {

            if (scrollToTop)
                binding.diaryListView.smoothScrollToPosition(0)
        }
    }

    private fun renderErrorFetchingDiaryListState(error: Throwable) {
        Log.e(TAG, "renderErrorFetchingDiaryListState: Error fetching", error)
    }

    private fun populateCalendarWithDiaryEntryDates(entries: List<Diary>) {
        val entryDates = entries.map {
            it.date.toLocalDate()
        }

        binding.diaryCalendarView.setEventDates(entryDates)

    }

    private fun renderLoadingState() {
        Log.d(TAG, "renderLoadingState: Loading Diaries")
    }

}