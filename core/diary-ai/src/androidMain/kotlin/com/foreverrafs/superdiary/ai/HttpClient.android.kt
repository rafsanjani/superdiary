package com.foreverrafs.superdiary.ai

import io.ktor.client.HttpClient

actual fun createHttpClient(): HttpClient = HttpClient(io.ktor.client.engine.okhttp.OkHttp)
