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
import com.foreverrafs.superdiary.list.presentation.detail.screen.DetailScreen
import com.foreverrafs.superdiary.list.presentation.list.DiaryListScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EntryProviderScope<NavKey>.DiaryListNavigation(
    onBackPress: () -> Unit,
    onAddEntry: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(
                        subclass = DiaryListRoute.DiaryListScreen::class,
                        serializer = DiaryListRoute.DiaryListScreen.serializer(),
                    )
                    subclass(
                        subclass = DiaryListRoute.DetailScreen::class,
                        serializer = DiaryListRoute.DetailScreen.serializer(),
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
            entry<DiaryListRoute.DetailScreen> { key ->
                DetailScreen(
                    diaryId = key.diaryId,
                    onProfileClick = onProfileClick,
                    onBackPress = { backStack.removeAt(backStack.lastIndex) },
                )
            }
            entry<DiaryListRoute.DiaryListScreen> {
                DiaryListScreen(
                    onAddEntry = onAddEntry,
                    onDiaryClick = {
                        backStack.add(DiaryListRoute.DetailScreen(it.toString()))
                    },
                    onProfileClick = onProfileClick,
                    onBackPress = onBackPress,
                )
            }
        },
    )
}
