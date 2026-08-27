package com.foreverrafs.superdiary.creatediary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.foreverrafs.superdiary.design.icons.SuperDiaryIcon
import com.mohamedrejeb.richeditor.model.RichTextState

@Composable
fun RichTextStyleRow(
    state: RichTextState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        RichTextStyleButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                    ),
                )
            },
            isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
            icon = SuperDiaryIcon.FormatBold,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                    ),
                )
            },
            isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
            icon = SuperDiaryIcon.FormatItalic,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(
                        textDecoration = TextDecoration.Underline,
                    ),
                )
            },
            isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
            icon = SuperDiaryIcon.FormatUnderlined,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(
                        textDecoration = TextDecoration.LineThrough,
                    ),
                )
            },
            isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
            icon = SuperDiaryIcon.FormatStrikethrough,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleUnorderedList()
            },
            isSelected = state.isUnorderedList,
            icon = SuperDiaryIcon.FormatListBulleted,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleOrderedList()
            },
            isSelected = state.isOrderedList,
            icon = SuperDiaryIcon.FormatListNumbered,
        )
    }
}
