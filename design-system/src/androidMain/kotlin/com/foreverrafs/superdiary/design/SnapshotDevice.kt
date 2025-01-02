package com.foreverrafs.superdiary.design

import app.cash.paparazzi.DeviceConfig
import com.android.resources.NightMode

@Suppress("unused")
enum class SnapshotDevice(val config: DeviceConfig) {
    PIXEL_6_LIGHT(DeviceConfig.Companion.PIXEL_6),
    PIXEL_6_DARK(DeviceConfig.Companion.PIXEL_6.copy(nightMode = NightMode.NIGHT)),
}
