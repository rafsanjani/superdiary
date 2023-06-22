package com.foreverrafs.common

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform