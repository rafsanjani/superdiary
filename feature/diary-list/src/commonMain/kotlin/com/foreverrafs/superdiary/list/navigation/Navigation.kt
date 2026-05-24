package com.foreverrafs.superdiary.list.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.foreverrafs.superdiary.list.presentation.list.DiaryListTab
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EntryProviderScope<NavKey>.DiaryListNavigation(
    onBackPress: () -> Unit,
    onAddEntry: () -> Unit,
    onDiaryClick: (diaryId: Long) -> Unit,
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        subclass = DiaryListRoute.DiaryListScreen::class,
                        serializer = DiaryListRoute.DiaryListScreen.serializer(),
                    )
                }
            }
        },
        DiaryListRoute.DiaryListScreen,
    )

    NavDisplay(
        backStack = backStack,
        onBack = onBackPress,
        entryDecorators = listOf<NavEntryDecorator<NavKey>>(
            rememberSaveableStateHolderNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<DiaryListRoute.DiaryListScreen> {
                DiaryListTab(
                    onAddEntry = onAddEntry,
                    onDiaryClick = onDiaryClick,
                    onBackPress = onBackPress,
                )
            }
        },
    )
}
