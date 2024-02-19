package com.foreverrafs.superdiary.core.analytics

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName(swiftName = "AnalyticsTracker")
fun interface AnalyticsTracker {
    fun trackEvent(event: AnalyticsEvents)
}
