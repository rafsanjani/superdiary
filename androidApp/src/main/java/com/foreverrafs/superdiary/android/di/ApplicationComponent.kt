/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreverrafs.superdiary.android.di

import android.app.Application
import android.content.Context
import com.foreverrafs.superdiary.android.DiaryApp
import com.foreverrafs.superdiary.diary.inject.DataComponent
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class ApplicationComponent(
    @get:Provides val application: Application,
) : DataComponent {
    abstract val getAllDiariesUseCase: GetAllDiariesUseCase

    companion object {
        fun from(context: Context): ApplicationComponent {
            return (context.applicationContext as DiaryApp).component
        }
    }
}