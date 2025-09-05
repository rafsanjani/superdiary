package com.foreverrafs.superdiary

import com.foreverrafs.superdiary.di.demoModule
import org.koin.core.module.Module

class DiaryApp : BaseDiaryApp() {
    override fun koinModules(): List<Module> = super.koinModules().toMutableList() +
        demoModule
}
