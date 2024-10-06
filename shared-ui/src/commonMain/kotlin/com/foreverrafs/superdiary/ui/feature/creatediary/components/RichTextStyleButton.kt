package com.foreverrafs.superdiary.ui.feature.creatediary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun RichTextStyleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    isSelected: Boolean = false,
) {
    IconButton(
        modifier = modifier
            // Workaround to prevent the rich editor
            // from losing focus when clicking on the button
            // (Happens only on Desktop)
            .focusProperties { canFocus = false },
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        ),
    ) {
        Icon(
            modifier = Modifier
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(4.dp),
                ),
            imageVector = icon,
            contentDescription = icon.name,
            tint = tint ?: LocalContentColor.current,
        )
    }
}
