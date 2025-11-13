package com.foreverrafs.superdiary.creatediary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.foreverrafs.superdiary.creatediary.screen.CreateDiaryScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun EntryProviderScope<NavKey>.CreateDiaryNavigation(
    onDiarySaveComplete: () -> Unit,
    onDiarySaveAbort: () -> Unit,
) {
    val savedStateConfiguration = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(CreateDiaryRoute.CreateDiary::class, CreateDiaryRoute.CreateDiary.serializer())
            }
        }
    }

    val backStack = rememberNavBackStack(
        savedStateConfiguration,
        CreateDiaryRoute.CreateDiary,
    )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeAt(backStack.lastIndex) },
        entryDecorators = listOf<NavEntryDecorator<NavKey>>(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<CreateDiaryRoute.CreateDiary> {
                CreateDiaryScreen(
                    onDiarySaveComplete = onDiarySaveComplete,
                    onDiarySaveAbort = onDiarySaveAbort,
                )
            }
        },
    )
}
