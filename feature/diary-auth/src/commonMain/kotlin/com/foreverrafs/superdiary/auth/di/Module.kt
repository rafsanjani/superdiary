package com.foreverrafs.superdiary.auth.di

import com.foreverrafs.superdiary.auth.changepassword.ChangePasswordViewModel
import com.foreverrafs.superdiary.auth.login.BiometricLoginScreenViewModel
import com.foreverrafs.superdiary.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.auth.register.DeeplinkContainer
import com.foreverrafs.superdiary.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.auth.reset.PasswordResetViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val diaryAuthModule: Module = module {
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::RegisterScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::PasswordResetViewModel)
    viewModelOf(::BiometricLoginScreenViewModel)

    singleOf(::DeeplinkContainer)
}
