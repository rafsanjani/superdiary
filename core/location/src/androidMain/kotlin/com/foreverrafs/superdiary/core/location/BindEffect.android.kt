package com.foreverrafs.superdiary.core.location

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.foreverrafs.superdiary.core.location.permission.Bindable
import com.foreverrafs.superdiary.core.location.permission.PermissionsControllerWrapper

/**
 * Copied from [dev.icerock.moko.permissions.compose.BindEffect()]
 */
@Composable
actual fun BindEffect(permissionsControllerWrapper: PermissionsControllerWrapper) {
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val context: Context = LocalContext.current

    LaunchedEffect(permissionsControllerWrapper, lifecycleOwner, context) {
        val activity: ComponentActivity = checkNotNull(context as? ComponentActivity) {
            "$context context is not instance of ComponentActivity"
        }

        (permissionsControllerWrapper as? Bindable)?.bind(activity)
    }
}
