// Copyright 2022, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package com.foreverrafs.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() {
        rule.collect(
            packageName = "com.foreverrafs.superdiary.app",
            includeInStartupProfile = true,
        ) {
            startActivityAndWait()

            //  Run through the main navigation items
            AppScenarios.mainNavigationItems(device)
        }
    }
}
