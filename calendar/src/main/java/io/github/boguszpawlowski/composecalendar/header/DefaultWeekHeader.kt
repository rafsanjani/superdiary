package io.github.boguszpawlowski.composecalendar.header

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Default implementation of month header, shows current month and year, as well as
 * 2 arrows for changing currently showed month
 */
@Composable
@Suppress("LongMethod")
public fun DefaultWeekHeader(
  weekState: WeekState,
  modifier: Modifier = Modifier,
) {
//  Row(
//    modifier = modifier.fillMaxWidth(),
//    horizontalArrangement = Arrangement.Center,
//    verticalAlignment = Alignment.CenterVertically,
//  ) {
//    IconButton(
//      modifier = Modifier.testTag("Decrement"),
//      onClick = { weekState.currentWeek = weekState.currentWeek.dec() }
//    ) {
//      Image(
//        imageVector = Icons.Default.KeyboardArrowLeft,
//        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
//        contentDescription = "Previous",
//      )
//    }
//    Text(
//      modifier = Modifier.testTag("MonthLabel"),
//      text = weekState.currentWeek.yearMonth.month
//        .getDisplayName(SHORT, Locale.getDefault())
//        .lowercase()
//        .replaceFirstChar { it.titlecase() },
//      style = MaterialTheme.typography.h4,
//    )
//    Spacer(modifier = Modifier.width(8.dp))
//    Text(
//      text = weekState.currentWeek.yearMonth.year.toString(),
//      style = MaterialTheme.typography.h4
//    )
//    IconButton(
//      modifier = Modifier.testTag("Increment"),
//      onClick = { weekState.currentWeek = weekState.currentWeek.inc() }
//    ) {
//      Image(
//        imageVector = Icons.Default.KeyboardArrowRight,
//        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
//        contentDescription = "Next",
//      )
//    }
//  }
}
