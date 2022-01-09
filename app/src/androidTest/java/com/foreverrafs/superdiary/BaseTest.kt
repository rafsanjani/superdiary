package com.foreverrafs.superdiary

import androidx.annotation.StringRes
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule


abstract class BaseTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun inject() {
        hiltRule.inject()
    }

    private fun getString(@StringRes resId: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
    }
}