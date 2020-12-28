package com.foreverrafs.superdiary.framework.presentation.detail

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.navArgs
import com.foreverrafs.superdiary.R
import com.foreverrafs.superdiary.databinding.FragmentDiaryDetailBinding
import com.foreverrafs.superdiary.framework.presentation.common.BaseFragment
import com.foreverrafs.superdiary.framework.presentation.util.invisible
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>() {
    private val incomingArgs by navArgs<DiaryDetailFragmentArgs>()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
    private val incomingDiary by lazy {
        incomingArgs.diary
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDiaryDetailBinding {
        return FragmentDiaryDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        processIncomingDiary()
    }

    private fun processIncomingDiary() {
        with(binding) {
            val date = incomingDiary.date.format(formatter)

            tvDairyEntryDate.text = date
            textDiaryEntry.setText(incomingDiary.message)

            if (incomingDiary.date.toLocalDate() != LocalDate.now()) {
                textDiaryEntry.inputType = InputType.TYPE_NULL
                btnEditDiary.invisible()

                //center the delete button if we are unable to edit
                val params =
                    CoordinatorLayout.LayoutParams(
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT,
                        CoordinatorLayout.LayoutParams.WRAP_CONTENT
                    )

                params.gravity = Gravity.BOTTOM or Gravity.CENTER
                params.bottomMargin = resources.getDimension(R.dimen.space).toInt()

                btnDeleteDiary.layoutParams = params
            }
        }
    }

}