package com.foreverrafs.superdiary.framework.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.superdiary.databinding.FragmentDiaryDetailBinding
import com.foreverrafs.superdiary.framework.presentation.common.BaseFragment
import com.foreverrafs.superdiary.framework.presentation.diarylist.DiaryListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>() {
    private val diaryListViewModel: DiaryListViewModel by viewModels()
    private lateinit var diaryPagerAdapter: DiaryDetailPagerAdapter

//    private val incomingArgs by navArgs<DiaryDetailFragmentArgs>()
//    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
//    private val incomingDiary by lazy {
//        incomingArgs.diary
//    }
    private lateinit var diaryList: List<Diary>

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDiaryDetailBinding {
        return FragmentDiaryDetailBinding.inflate(inflater, container, false)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
//        super.onViewCreated(view, savedInstanceState)
//
//        diaryEntryPager.setPageTransformer(RotateDownPageTransformer())
//
//        next.setOnClickListener {
//            diaryEntryPager.currentItem =
//                (diaryEntryPager.currentItem + 1).coerceAtMost(diaryList.size - 1)
//        }
//
//        previous.setOnClickListener {
//            diaryEntryPager.currentItem = (diaryEntryPager.currentItem - 1).coerceAtLeast(0)
//        }
//
//        lifecycleScope.launchWhenStarted {
////            diaryListViewModel.viewState.collect {
////                when (it) {
////                    is DiaryListStateRafs.Loaded -> {
////                        diaryList = diaryListViewModel.allDiaries.reversed()
////                        diaryPagerAdapter = DiaryDetailPagerAdapter(diaryList)
////                        diaryEntryPager.adapter = diaryPagerAdapter
////                        processIncomingDiary()
////                    }
////
////                    else -> {
////
////                    }
////                }
////            }
//        }
//
////        diaryEntryPager.registerOnPageChangeCallback(object :
////            ViewPager2.OnPageChangeCallback() {
////            override fun onPageSelected(position: Int) {
////                val diary = diaryList[position]
////                val date = diary.date.format(formatter)
////                tvDairyEntryDate.text = date
////
////                binding.next.isVisible = position < diaryList.size - 1
////                binding.previous.isVisible = position > 0
////            }
////        })
//    }

//
//    private fun processIncomingDiary() = lifecycleScope.launchWhenStarted {
//        with(binding) {
//            val date = incomingDiary.date.format(formatter)
//            tvDairyEntryDate.text = date
//
//            val position = diaryList.indexOf(incomingDiary)
//            binding.diaryEntryPager.currentItem = position
//        }
//    }

}