package com.foreverrafs.superdiary.data.di

import com.foreverrafs.superdiary.DarwinDatabaseDriver
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.data.DatabaseDriver
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.utils.AppleDiaryPreference
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun platformModule(analyticsTracker: AnalyticsTracker): Module = module {
    singleOf<DatabaseDriver>(::DarwinDatabaseDriver)
    single<AnalyticsTracker> { analyticsTracker }
    singleOf<DiaryPreference>(::AppleDiaryPreference)
}
