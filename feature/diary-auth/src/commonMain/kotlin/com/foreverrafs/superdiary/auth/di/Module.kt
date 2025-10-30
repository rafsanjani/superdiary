package com.foreverrafs.superdiary.auth.di

import com.foreverrafs.superdiary.auth.changepassword.ChangePasswordViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryAuthModule: Module = module {
    viewModelOf(::ChangePasswordViewModel)
}
