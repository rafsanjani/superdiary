package com.foreverrafs.superdiary.framework.presentation.diarylist

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView

class ItemTouchHelperCallback(private val onSwiped: (position: Int) -> Unit) :
    ItemTouchHelper.SimpleCallback(0, LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        return onSwiped(viewHolder.adapterPosition)
    }

}