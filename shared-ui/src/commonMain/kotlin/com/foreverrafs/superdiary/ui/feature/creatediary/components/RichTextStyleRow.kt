package com.foreverrafs.superdiary.ui.feature.creatediary.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
            icon = Icons.Outlined.FormatBold,
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
            icon = Icons.Outlined.FormatItalic,
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
            icon = Icons.Outlined.FormatUnderlined,
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
            icon = Icons.Outlined.FormatStrikethrough,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleUnorderedList()
            },
            isSelected = state.isUnorderedList,
            icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleOrderedList()
            },
            isSelected = state.isOrderedList,
            icon = Icons.Outlined.FormatListNumbered,
        )
    }
}
