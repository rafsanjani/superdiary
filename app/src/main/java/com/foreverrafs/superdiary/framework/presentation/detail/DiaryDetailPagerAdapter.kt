package com.foreverrafs.superdiary.framework.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foreverrafs.domain.feature_diary.model.Diary
import com.foreverrafs.superdiary.databinding.ItemDiaryDetailEntryBinding

class DiaryDetailPagerAdapter(
    private val diaries: List<Diary>
) :
    RecyclerView.Adapter<DiaryDetailPagerAdapter.DiaryViewHolder>() {


    override fun getItemCount(): Int {
        return diaries.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDiaryDetailEntryBinding.inflate(inflater, parent, false)

        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(diaries[position])
    }


    inner class DiaryViewHolder(private val binding: ItemDiaryDetailEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary) = with(binding) {
            textDiaryEntry.setText(diary.message)
        }
    }
}