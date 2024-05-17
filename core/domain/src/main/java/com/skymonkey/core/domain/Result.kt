package com.skymonkey.core.domain

/**
 * Wraps successful or erroneous results
 */
sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: com.skymonkey.core.domain.Error>(val error: E): Result<Nothing, E>
}

/**
 * extension function for Result interface which maps from one data type to another.
 * <T, E: Error, R> T is data type, E is error type, R is data type we want to map to (e.g int to String)
 */
inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

/**
 * Converts result which contains data, to a result which contains no data (Unit)
 */
fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyDataResult<E> {
    return map { } // just maps to Unit type (empty)
}

/**
 * When we don't have data to return.
 * e.g logging-in may be successful, but there may be no data to return.
 */
typealias EmptyDataResult<E> = Result<Unit, E>