package com.foreverrafs.superdiary.android.navigation

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import com.foreverrafs.superdiary.android.AppTheme
import com.foreverrafs.superdiary.android.screens.NavGraphs
import com.foreverrafs.superdiary.android.screens.appCurrentDestinationAsState
import com.foreverrafs.superdiary.android.screens.destinations.Destination
import com.foreverrafs.superdiary.android.screens.startAppDestination
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun BottomBar(
    navController: NavController
) {
    val currentDestination: Destination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.app.startAppDestination

    NavigationBar {
        BottomBarDestination.values().forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                        launchSingleTop = true
                    })
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                    )
                },
            )
        }
    }

}

@Composable
@Preview(
    name = "Night Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Day Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
private fun Preview() {
    AppTheme {
        BottomBar(navController = rememberNavController())
    }
}