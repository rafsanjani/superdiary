package com.foreverrafs.superdiary.ui.creatediary

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.UndoableRichTextState
import kotlin.test.Test

class RichTextStateTest {
    private val state = UndoableRichTextState()

    @Test
    fun `Should undo to a past state`() {
        state.richTextState.setText("First")
        state.save()
        state.richTextState.setText("Second")
        state.undo()

        assertThat(state.richTextState.annotatedString.text).isEqualTo("First")
    }

    @Test
    fun `Should redo to a future state`() {
        state.richTextState.setText("First")
        state.save()
        state.richTextState.setText("Second")
        state.save()
        state.undo()
        state.redo()

        assertThat(state.richTextState.annotatedString.text).isEqualTo("Second")
    }
}
