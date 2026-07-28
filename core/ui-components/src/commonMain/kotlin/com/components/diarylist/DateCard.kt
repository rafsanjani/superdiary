package com.components.diarylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.common.utils.format
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import superdiary.core.ui_components.generated.resources.Res
import superdiary.core.ui_components.generated.resources.diary_entry_content_description

@Composable
internal fun DateCard(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    val formattedDate = date.format("EEE dd MMMM yyyy")
    val entryContentDescription = stringResource(
        Res.string.diary_entry_content_description,
        formattedDate,
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 0.dp,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.semantics {
                contentDescription = entryContentDescription
            },
            text = buildDateAnnotatedString(date),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
