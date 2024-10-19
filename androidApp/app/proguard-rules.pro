# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn androidx.test.platform.app.InstrumentationRegistry
-keepattributes EnclosingMethod

-dontwarn io.ktor.client.network.sockets.SocketTimeoutException
-dontwarn io.ktor.client.plugins.HttpRequestRetry$Configuration
-dontwarn io.ktor.client.plugins.HttpRequestRetry$Plugin
-dontwarn io.ktor.client.plugins.HttpRequestRetry$ShouldRetryContext
-dontwarn io.ktor.client.plugins.HttpRequestRetry
-dontwarn io.ktor.client.plugins.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.plugins.HttpTimeout$Plugin
-dontwarn io.ktor.client.plugins.HttpTimeout
-dontwarn io.ktor.client.plugins.observer.ResponseObserver$Plugin
-dontwarn io.ktor.client.plugins.observer.ResponseObserver
-dontwarn io.ktor.util.KtorDsl
-dontwarn io.ktor.utils.io.CoroutinesKt
-dontwarn io.ktor.utils.io.core.ByteReadPacket
-dontwarn io.ktor.utils.io.core.Input
