package com.foreverrafs.superdiary.favorite.di

import com.foreverrafs.superdiary.favorite.FavoriteViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val favoriteModule: Module = module {
    viewModelOf(::FavoriteViewModel)
}
