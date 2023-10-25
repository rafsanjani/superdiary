package com.foreverrafs.superdiary.ui.feature.creatediary.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.FormatAlignCenter
import androidx.compose.material.icons.outlined.FormatAlignLeft
import androidx.compose.material.icons.outlined.FormatAlignRight
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.RichTextState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RichTextStyleRow(
    modifier: Modifier = Modifier,
    state: RichTextState,
) {
    FlowRow(
        modifier = modifier,
    ) {
        RichTextStyleButton(
            onClick = {
                state.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Left,
                    ),
                )
            },
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Left,
            icon = Icons.Outlined.FormatAlignLeft,
        )

        RichTextStyleButton(
            onClick = {
                state.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Center,
                    ),
                )
            },
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
            icon = Icons.Outlined.FormatAlignCenter,
        )

        RichTextStyleButton(
            onClick = {
                state.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Right,
                    ),
                )
            },
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Right,
            icon = Icons.Outlined.FormatAlignRight,
        )

        RichTextStyleButton(
            onClick = {
                state.addParagraphStyle(
                    ParagraphStyle(
                        textAlign = TextAlign.Justify,
                    ),
                )
            },
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Justify,
            icon = Icons.Outlined.FormatAlignRight,
        )

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
                state.toggleSpanStyle(
                    SpanStyle(
                        color = Color.Green,
                    ),
                )
            },
            isSelected = state.currentSpanStyle.color == Color.Red,
            icon = Icons.Filled.Circle,
            tint = Color.Red,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleSpanStyle(
                    SpanStyle(
                        background = Color.Yellow,
                    ),
                )
            },
            isSelected = state.currentSpanStyle.background == Color.Yellow,
            icon = Icons.Outlined.Circle,
            tint = Color.Yellow,
        )

        RichTextStyleButton(
            onClick = {
                state.toggleUnorderedList()
            },
            isSelected = state.isUnorderedList,
            icon = Icons.Outlined.FormatListBulleted,
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
