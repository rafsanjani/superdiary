@file:Suppress("unused")

package com.foreverrafs.superdiary.android.di

import android.app.Application
import android.content.Context
import com.foreverrafs.superdiary.android.DiaryApp
import com.foreverrafs.superdiary.diary.inject.DataComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
abstract class ApplicationComponent(
    @get:Provides val application: Application,
) : DataComponent {
    abstract val appScreens: AppScreens

    companion object {
        fun from(context: Context): ApplicationComponent {
            return (context.applicationContext as DiaryApp).component
        }
    }
}
