package com.foreverrafs.superdiary.ui.feature.diarylist.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun DiaryHeader(
    modifier: Modifier = Modifier,
    text: String,
    inSelectionMode: Boolean,
    selectGroup: () -> Unit,
    deSelectGroup: () -> Unit,
    selected: Boolean,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = text,
                style = MaterialTheme.typography.headlineMedium,
            )

            if (inSelectionMode) {
                if (selected) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 4.dp)
                            .size(20.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape,
                            )
                            .clip(CircleShape)
                            .clickable {
                                deSelectGroup()
                            },
                    )
                } else {
                    Icon(
                        Icons.Filled.RadioButtonUnchecked,
                        tint = Color.Black.copy(alpha = 0.55f),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 4.dp)
                            .size(20.dp)
                            .clickable {
                                selectGroup()
                            },
                    )
                }
            }
        }
    }
}
