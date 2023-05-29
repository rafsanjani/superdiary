package com.foreverrafs.superdiary.android

import android.app.Application
import com.foreverrafs.superdiary.android.di.ApplicationComponent
import com.foreverrafs.superdiary.android.di.create

class DiaryApp : Application() {
    val component: ApplicationComponent by lazy { ApplicationComponent::class.create(this) }
}
