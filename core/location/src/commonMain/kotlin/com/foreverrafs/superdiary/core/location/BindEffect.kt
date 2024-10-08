package com.foreverrafs.superdiary.core.location

import androidx.compose.runtime.Composable
import com.foreverrafs.superdiary.core.location.permission.PermissionsControllerWrapper

@Composable
expect fun BindEffect(permissionsControllerWrapper: PermissionsControllerWrapper)
