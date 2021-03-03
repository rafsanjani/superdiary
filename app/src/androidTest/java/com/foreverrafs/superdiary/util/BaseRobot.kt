package com.foreverrafs.superdiary.util

import android.view.View
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.foreverrafs.superdiary.util.EspressoExtensions.searchFor
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher


@Suppress("unused")
open class BaseRobot {
    fun tapViewWithId(@IntegerRes viewId: Int, wait: Boolean = false): ViewInteraction {
        val matcher = withId(viewId)

        return tapView(matcher, wait)
    }

    private fun tapView(matcher: Matcher<View>, wait: Boolean = false): ViewInteraction {
        return if (wait) {
            waitForView(matcher).perform(click())
        } else {
            onView(matcher).perform(click())
        }
    }


    fun tapViewWithText(text: String, wait: Boolean = false): ViewInteraction {
        val matcher = withText(text)

        return tapView(matcher, wait)
    }

    fun verifyViewContainsText(@IntegerRes resId: Int, text: String): ViewInteraction {
        return onView(
            withId(resId)
        ).check(matches(withText(CoreMatchers.containsString(text))))
    }

    /**
     * Verifies that the RecyclerView list contains an item with [text]
     * [viewHolderContainerRootViewId] the id of the topmost container of the [RecyclerView.ViewHolder] layout
     */
    fun verifyListItemWithText(
        @IntegerRes viewHolderContainerRootViewId: Int,
        text: String,
    ): ViewInteraction {
        return onView(
            CoreMatchers.allOf(
                isDescendantOfA(
                    withId(viewHolderContainerRootViewId)
                ),
                withText(text),
                isCompletelyDisplayed()
            )
        ).check(matches(withText(CoreMatchers.containsString(text))))
    }

    fun tapItemInList(@IntegerRes resId: Int, position: Int): ViewInteraction {
        return onView(withId(resId))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    click()
                )
            )
    }


    /**
     * Taps an item in a [RecyclerView] list containing [text]
     * [viewHolderContainerRootViewId] the id of the topmost container of the [RecyclerView.ViewHolder] layout
     */
    fun tapItemInList(
        @IntegerRes viewHolderContainerRootViewId: Int,
        text: String
    ) {
        onView(
            CoreMatchers.allOf(
                isDescendantOfA(
                    withId(viewHolderContainerRootViewId)
                ),
                withText(text),
                isCompletelyDisplayed()
            )
        ).perform(click())
    }

    fun String.togo() = "@3"

    fun typeText(@IntegerRes viewId: Int, text: String): ViewInteraction {
        return onView(withId(viewId)).perform(
            replaceText(text),
            closeSoftKeyboard()
        )
    }

    fun swipeLeft(@IntegerRes viewId: Int): ViewInteraction {
        return onView(withId(viewId)).perform(
            swipeLeft()
        )
    }

    fun verifyViewWithIdIsDisplayed(@IntegerRes viewId: Int) {
        onView(withId(viewId)).check(
            matches(
                isCompletelyDisplayed()
            )
        )
    }

    /**
     * Verifies that a view containing the exact [text] is visible
     */
    fun verifyViewWithTextIsDisplayed(text: String) {
        val matcher = withText(text)
        waitForView(matcher).check(matches(isCompletelyDisplayed()))
    }

    /**
     * Verifies that a view containing [text] is displayed.
     * This will also return a match when [text] is a substring of the target view
     */
    fun verifyViewContainsTextIsDisplayed(text: String) {
        val matcher = withText(containsString(text))

        waitForView(matcher).check(matches(isCompletelyDisplayed()))
    }

    fun verifyViewWithIdIsNotDisplayed(@IntegerRes viewId: Int) {
        onView(withId(viewId))
            .check(
                matches(
                    not(isCompletelyDisplayed())
                )
            )
    }

    fun swipeRight(@IntegerRes viewId: Int): ViewInteraction {
        return onView(withId(viewId)).perform(
            swipeRight()
        )
    }

    private fun waitForView(
        viewMatcher: Matcher<View>,
        waitMillis: Int = 5000,
        waitMillisPerTry: Long = 100
    ): ViewInteraction {

        // Derive the max tries
        val maxTries = waitMillis / waitMillisPerTry.toInt()

        var tries = 0

        for (i in 0..maxTries)
            try {
                // Track the amount of times we've tried
                tries++

                // Search the root for the view
                onView(isRoot()).perform(searchFor(viewMatcher))

                // If we're here, we found our view. Now return it
                return onView(viewMatcher)

            } catch (e: NoMatchingViewException) {
                if (tries == maxTries) {
                    throw e
                }
                Thread.sleep(waitMillisPerTry)
            }

        throw Exception("Error finding a view matching $viewMatcher")
    }
}