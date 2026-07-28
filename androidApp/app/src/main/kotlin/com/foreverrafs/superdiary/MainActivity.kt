package com.foreverrafs.superdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import com.foreverrafs.superdiary.auth.register.AuthDeepLink
import com.foreverrafs.superdiary.auth.register.AuthDeepLinkMatch
import com.foreverrafs.superdiary.auth.register.InvalidAuthDeepLinkMatch
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.ui.App
import com.foreverrafs.superdiary.ui.AppLaunchContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.serializer
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val supabase: SupabaseClient by inject()
    private val logger: AggregateLogger by inject()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val launchContext = resolveLaunchContext()

        setContent {
            enableEdgeToEdge()

            App(launchContext = launchContext)
        }
    }

    private fun resolveLaunchContext(): AppLaunchContext {
        val request = DeepLinkRequest(intent)
        val callbackUri = with(supabase.auth.config) { "$scheme://$host/" }
        val match = authDeepLinkMatchers(callbackUri)
            .firstNotNullOfOrNull { it.match(request) }
            ?: return AppLaunchContext()
        val uri = request.uri ?: return AppLaunchContext()
        val linkType = when (val key = match.key) {
            is AuthDeepLinkMatch ->
                AuthDeepLink.LinkType.entries
                    .firstOrNull { it.type == key.type }
                    ?: return AppLaunchContext()

            InvalidAuthDeepLinkMatch -> AuthDeepLink.LinkType.Invalid

            else -> return AppLaunchContext()
        }

        logger.i("MainActivity") {
            "Navigation 3 matched an auth deep link. Attempting to resolve its payload"
        }

        return AppLaunchContext(
            deepLink = AuthDeepLink(
                type = linkType,
                payload = uri,
            ),
        )
    }
}

private fun authDeepLinkMatchers(callbackUri: String): List<DeepLinkMatcher<Any>> = listOf(
    UriDeepLinkMatcher(
        uriPattern = DeepLinkUri("$callbackUri#.*type={type}"),
        serializer = serializer<AuthDeepLinkMatch>(),
    ),
    UriDeepLinkMatcher(
        uriPattern = DeepLinkUri(callbackUri),
        serializer = serializer<InvalidAuthDeepLinkMatch>(),
    ),
)
