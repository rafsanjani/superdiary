package com.foreverrafs.superdiary.ui

import app.cash.paparazzi.DeviceConfig
import com.android.resources.NightMode

@Suppress("unused")
enum class SnapshotDevice(val config: DeviceConfig) {
    PIXEL_6_LIGHT(DeviceConfig.PIXEL_6),
    PIXEL_6_DARK(DeviceConfig.PIXEL_6.copy(nightMode = NightMode.NIGHT)),
}
