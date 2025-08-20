package com.foreverrafs.superdiary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.foreverrafs.superdiary.auth.register.DeeplinkContainer
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.ui.App
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val deeplinkContainer: DeeplinkContainer by inject()
    private val supabase: SupabaseClient by inject()
    private val logger: AggregateLogger by inject()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        processAuthDeeplink()

        setContent {
            enableEdgeToEdge()

            App(
                modifier = Modifier.semantics {
                    testTagsAsResourceId = true
                },
            )
        }
    }

    /**
     * Extract and add the url for the deeplink into a singleton container. The
     * actual processing of the deeplink will happen in the common module
     */
    private fun processAuthDeeplink() {
        val data = intent.data ?: return
        val scheme = data.scheme ?: return
        val host = data.host ?: return
        if (scheme != supabase.auth.config.scheme || host != supabase.auth.config.host) return
        logger.d("MainActivity") {
            "Found an app related deeplink. Attempting to resolve it's payload"
        }
        deeplinkContainer.push(
            data,
        )
    }
}
