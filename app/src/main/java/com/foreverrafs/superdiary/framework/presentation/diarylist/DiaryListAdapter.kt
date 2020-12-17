package com.foreverrafs.superdiary.framework.presentation.diarylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.foreverrafs.superdiary.business.model.Diary
import com.foreverrafs.superdiary.databinding.ItemDiaryBinding
import java.time.format.DateTimeFormatter

/**
 * We are unable to use a [ListAdapter] because filtering the original list and resubmitting requires some not-so-optimal
 * strategies to overcome the "inconsistencies" [IndexOutOfBoundsException]
 *
 * One solution is to call [ListAdapter.submitList] with null, followed by the filtered list but we'd rather use a [RecyclerView.Adapter]
 * with a custom []
 */
class DiaryListAdapter(private val onDelete: (diary: Diary) -> Unit) :
    RecyclerView.Adapter<DiaryListAdapter.DiaryViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean =
            oldItem.id == newItem.id
    }
    private val diffUtil = AsyncListDiffer(this, callback)

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a")

    private val itemTouchHelperCallback = ItemTouchHelperCallback(onSwiped = { position ->
        val diaryToDelete = diffUtil.currentList[position]
        onDelete(diaryToDelete)
    })

    override fun getItemCount(): Int {
        return diffUtil.currentList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val callback = ItemTouchHelper(itemTouchHelperCallback)

        callback.attachToRecyclerView(recyclerView)
    }

    fun submitList(list: List<Diary>, callback: () -> Unit = {}) {
        //Creating a new instance of the list always for diffing to occur
        diffUtil.submitList(list.toList(), callback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDiaryBinding.inflate(inflater, parent, false)

        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(diffUtil.currentList[position])
    }


    inner class DiaryViewHolder(private val binding: ItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(diary: Diary) = with(binding) {
            text.text = diary.message
            diaryTime.text = formatter.format(diary.date)
        }
    }
}