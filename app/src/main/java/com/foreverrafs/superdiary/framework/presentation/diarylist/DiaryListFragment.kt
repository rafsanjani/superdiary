package com.foreverrafs.superdiary.framework.presentation.diarylist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.databinding.FragmentDiaryListBinding
import com.foreverrafs.superdiary.framework.presentation.common.BaseFragment
import com.foreverrafs.superdiary.framework.presentation.diarylist.state.DiaryListState
import com.foreverrafs.superdiary.framework.presentation.util.invisible
import com.foreverrafs.superdiary.framework.presentation.util.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import kotlinx.coroutines.flow.collect
import java.time.LocalDate

private const val TAG = "DiaryListFragment"

@AndroidEntryPoint
class DiaryListFragment : BaseFragment<FragmentDiaryListBinding>() {
    private val diaryListViewModel: DiaryListViewModel by activityViewModels()
    private val diaryListAdapter =
        DiaryListAdapter(onDiaryDeleted = ::onDiaryDeleted, onDiaryClicked = ::onDiaryClicked)

    private val today = LocalDate.now()
    private var selectedDate = today


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

        binding.settings.setOnClickListener {
            navController.navigate(
                DiaryListFragmentDirections.actionDiaryListFragmentToSettingsFragment()
            )
        }

        diaryCalendarView.addOnDateSelectedListener { selectedDate ->
            this@DiaryListFragment.selectedDate = selectedDate

            diaryListViewModel.setSelectedDateForDiaries(selectedDate)

            diaryListViewModel.getDiariesForDate(selectedDate)

            if (selectedDate == today)
                btnNewEntry.show()
            else
                btnNewEntry.hide()
        }

        diaryCalendarView.addOnMonthChangedListener {
            when (it.month) {
                diaryListViewModel.dateForSelectedDiary.month -> {
                    diaryCalendarView.selectDate(diaryListViewModel.dateForSelectedDiary)
                }
                today.month -> {
                    diaryCalendarView.selectDate(today)
                }
                else -> {
                    val firstDayOfMonth = LocalDate.of(it.year, it.month, 1)
                    diaryCalendarView.selectDate(firstDayOfMonth)
                }
            }
        }


        btnNewEntry.setOnClickListener {
            navController.navigate(
                DiaryListFragmentDirections.actionDiaryListFragmentToAddDiaryDialogFragment()
            )
        }

        setupDiaryList()
        observeDiaryList()
    }

    override fun onResume() {
        super.onResume()

        binding.diaryCalendarView.selectDate(diaryListViewModel.dateForSelectedDiary)
    }

    private fun renderEmptyListState() = with(binding) {
        //we still need to show the entry dots on the map if we still have entries in the database
        if (diaryListViewModel.allDiaries.isNotEmpty())
            diaryCalendarView.setEventDates(diaryListViewModel.allDiaries.map { it.date.toLocalDate() })


        diaryListAdapter.submitList(emptyList())

        tvEmptyMessage.setText(R.string.label_no_diary_entry)

        tvEmptyMessage.visible()
    }


    private fun onDiaryDeleted(diary: Diary) {
        //remove the item to be deleted from the list
        val list = diaryListViewModel.allDiaries
            .filter {
                it.date.toLocalDate() == diary.date.toLocalDate()
            }

        //show new list without item
        renderDiaryListState(
            list.toMutableList().also {
                it.remove(diary)
            }
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Diary Entry")
            .setMessage("Are you sure you want to delete this diary entry?")
            .setPositiveButton(
                R.string.alert_delete
            ) { _, _ ->
                //Actually delete the item from the data source
                diaryListViewModel.deleteDiary(diary)
            }.setNegativeButton(android.R.string.cancel) { _, _ ->
                //show back list containing original item because we didn't delete
                renderDiaryListState(list)
            }
            .show()
    }

    private fun onDiaryClicked(diary: Diary) {
        navController.navigate(
            DiaryListFragmentDirections.actionDiaryListFragmentToDiaryDetailFragment(diary)
        )
    }

    private fun setupDiaryList() = with(binding) {
        diaryListView.adapter = diaryListAdapter

        diaryListView.itemAnimator = FadeInDownAnimator()

        diaryListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy - dx > 0) {
                    binding.btnNewEntry.hide()
                } else {
                    if (diaryListViewModel.dateForSelectedDiary == today)
                        binding.btnNewEntry.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE ->
                        if (diaryListViewModel.dateForSelectedDiary == today)
                            binding.btnNewEntry.show()
                }
            }
        })
    }

    private fun observeDiaryList() {
        lifecycleScope.launchWhenStarted {
            diaryListViewModel.viewState.collect { state ->
                if (state == null) return@collect

                when (state) {
                    is DiaryListState.DiaryList -> {
                        renderDiaryListState(state.list)

                        populateCalendarWithDiaryEntryDates(diaryListViewModel.allDiaries)
                    }
                    is DiaryListState.Error -> renderErrorFetchingDiaryListState(state.error)
                    DiaryListState.Loading -> renderLoadingState()
                    is DiaryListState.Deleted -> {
                    }
                    DiaryListState.Empty -> {
                        renderEmptyListState()
                    }
                }
            }
        }
    }


    private fun renderDiaryListState(diaries: List<Diary>, scrollToTop: Boolean = true) {
        binding.tvEmptyMessage.invisible()

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