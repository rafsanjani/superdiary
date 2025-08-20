package com.foreverrafs.superdiary.list

import com.foreverrafs.superdiary.domain.model.Diary

/**
 * Encapsulates all actions that can be performed on a list of diary items
 *
 * @param onAddEntry Add a new diary entry to the datasource
 * @param onDeleteDiaries Delete all the diary entries with the selected
 *     ids
 * @param onToggleFavorite Add the specified diary to the favorites if it
 *     isn't already in there, and remove it otherwise
 * @param onApplyFilters Apply selection filters to the list
 * @param onAddSelection Add the entry with the specified id to the
 *     selected list of items
 * @param onDiaryClicked Process a click event on a specified diary
 * @param onRemoveSelection Remove the entry specified with the id from the
 *     list of selected items
 * @param onToggleSelection Select the specified diary item if it isn't
 *     selected and deselect it otherwise
 * @param onCancelSelection Deselect all selected items and exit selection
 *     mode
 * @param onBackPressed When the back action is invoked either through the
 *     system back button or the AppBar back button
 */
data class DiaryListActions(
    val onAddEntry: () -> Unit,
    val onDeleteDiaries: suspend (selectedIds: List<Diary>) -> Boolean,
    val onToggleFavorite: suspend (diary: Diary) -> Boolean,
    val onApplyFilters: (filters: DiaryFilters) -> Unit,
    val onAddSelection: ((id: Long?) -> Unit) = {},
    val onDiaryClicked: (diaryId: Long) -> Unit,
    val onRemoveSelection: (id: Long?) -> Unit = {},
    val onToggleSelection: (id: Long?) -> Unit = {},
    val onCancelSelection: () -> Unit = {},
    val onBackPressed: () -> Unit = {},
) {
    companion object
}
