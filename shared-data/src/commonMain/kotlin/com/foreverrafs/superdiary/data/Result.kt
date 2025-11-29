package com.foreverrafs.superdiary.data

/**
 * A sealed interface representing a result of an operation, which can either be a [Success] or a [Failure].
 * This is a more expressive way to handle errors than throwing exceptions.
 */
sealed interface Result<out T> {
    /**
     * Represents a successful result.
     * @param data The data returned by the operation.
     */
    data class Success<T>(val data: T) : Result<T>

    /**
     * Represents a failed result.
     * @param error The error that occurred during the operation.
     */
    data class Failure(val error: Throwable) : Result<Nothing>
}

/**
 * Returns `true` if this is a [Result.Success], `false` otherwise.
 */
val <T> Result<T>.isSuccess: Boolean
    get() = this is Result.Success

/**
 * Returns `true` if this is a [Result.Failure], `false` otherwise.
 */
val <T> Result<T>.isFailure: Boolean
    get() = this is Result.Failure

/**
 * Returns the encapsulated value if this instance represents [Result.Success] or `null`
 * if it is [Result.Failure].
 */
fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Failure -> null
}

/**
 * Returns the encapsulated [Throwable] exception if this instance represents [Result.Failure] or `null`
 * if it is [Result.Success].
 */
fun <T> Result<T>.exceptionOrNull(): Throwable? = when (this) {
    is Result.Success -> null
    is Result.Failure -> error
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [Result.Success].
 * Returns the original [Result] unchanged.
 */
inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [Result.Failure].
 * Returns the original [Result] unchanged.
 */
inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    if (this is Result.Failure) action(error)
    return this
}

/**
 * Returns the encapsulated value if this instance represents [Result.Success] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [Result.Failure].
 */
inline fun <R, T : R> Result<T>.getOrElse(onFailure: (exception: Throwable) -> R): R = when (this) {
    is Result.Success -> data
    is Result.Failure -> onFailure(error)
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [Result.Success]
 * or the result of [onFailure] function for the encapsulated [Throwable] exception if it is [Result.Failure].
 */
inline fun <R, T> Result<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R,
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Failure -> onFailure(error)
}

/**
 * Returns a new [Result] with the result of [transform] function applied to the encapsulated value
 * if this instance represents [Result.Success].
 * Returns the original [Result.Failure] unchanged.
 */
inline fun <R, T> Result<T>.map(transform: (value: T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}

/**
 * Returns a new [Result] with the result of [transform] function applied to the encapsulated value
 * if this instance represents [Result.Success].
 * If [transform] throws an exception, it is caught and a [Result.Failure] is returned.
 * Returns the original [Result.Failure] unchanged.
 */
inline fun <R, T> Result<T>.mapCatching(transform: (value: T) -> R): Result<R> = when (this) {
    is Result.Success -> asResult { transform(data) }
    is Result.Failure -> this
}

/**
 * Helper function to run a block of code and wrap the result in a [Result] object.
 */
inline fun <T> asResult(block: () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Throwable) {
    Result.Failure(e)
}
