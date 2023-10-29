package com.foreverrafs.superdiary.ui.feature.diarylist

import com.foreverrafs.superdiary.diary.model.Diary

data class DiaryListActions(
    val onAddEntry: () -> Unit,
    val onDeleteDiaries: suspend (selectedIds: List<Diary>) -> Boolean,
    val onToggleFavorite: suspend (diary: Diary) -> Boolean,
    val onApplyFilters: (filters: DiaryFilters) -> Unit,
    val onAddSelection: ((id: Long?) -> Unit) = { },
    val onDiaryClicked: (diary: Diary) -> Unit,
    val onRemoveSelection: (id: Long?) -> Unit = {},
    val onToggleSelection: (id: Long?) -> Unit = {},
    val onCancelSelection: () -> Unit = {},
) {
    companion object
}
