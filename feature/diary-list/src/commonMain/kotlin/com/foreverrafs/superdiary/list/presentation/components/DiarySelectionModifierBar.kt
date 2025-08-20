package com.foreverrafs.superdiary.list.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
internal fun DiarySelectionModifierBar(
    inSelectionMode: Boolean,
    onDelete: (selectedIds: Set<Long>) -> Unit,
    onCancelSelection: () -> Unit,
    selectedIds: Set<Long>,
) {
    AnimatedVisibility(
        visible = inSelectionMode,
        modifier = Modifier
            .border(
                width = Dp.Hairline,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(8.dp),
            )
            .zIndex(1f)
            .fillMaxWidth()
            .wrapContentHeight(),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        // The outer surface will block inputs from propagating to the searchbar behind it
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onCancelSelection()
                            }
                            .padding(8.dp),
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    // Only show selected diaries when we have an item selected to prevent
                    // weird Selected Diaries: 0 from showing
                    if (selectedIds.isNotEmpty()) {
                        Text("Selected Diaries: ${selectedIds.size}")
                    }
                }

                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            onDelete(selectedIds)
                        }
                        .padding(8.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                )
            }
        }
    }
}
