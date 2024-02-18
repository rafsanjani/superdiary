package com.foreverrafs.superdiary.data

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Failure(val error: Throwable) : Result<Nothing>
}
