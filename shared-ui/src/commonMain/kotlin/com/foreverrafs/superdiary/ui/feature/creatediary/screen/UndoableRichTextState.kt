package com.foreverrafs.superdiary.ui.feature.creatediary.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.mohamedrejeb.richeditor.model.RichTextState

class UndoableRichTextState {
    private val undoHistory = ArrayDeque<String>()
    private val redoHistory = ArrayDeque<String>()

    val richTextState: RichTextState by mutableStateOf(RichTextState())
    fun undo() {
        val contents = undoHistory.removeLast()
        redoHistory.addLast(contents)
        richTextState.setHtml(contents)
    }

    fun redo() {
        val contents = redoHistory.removeLast()
        richTextState.setHtml(contents)
    }

    fun save() {
        if (!undoHistory.contains(richTextState.toHtml())) {
            undoHistory.addLast(richTextState.toHtml())
        }
    }
}

@Composable
fun rememberUndoableRichTextState(): UndoableRichTextState = remember {
    UndoableRichTextState()
}
