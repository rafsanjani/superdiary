package com.foreverrafs.superdiary

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform