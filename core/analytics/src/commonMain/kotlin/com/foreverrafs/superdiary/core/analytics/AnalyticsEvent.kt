package com.foreverrafs.superdiary.core.analytics

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "AnalyticsEvent")
interface AnalyticsEvent {
    val name: String
    val parameters: Map<String, String>
}
