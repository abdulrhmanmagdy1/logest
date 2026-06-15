package com.edham.logistics.core.utils

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        Loading -> throw IllegalStateException("Result is still loading")
    }
}

inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (exception: Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

suspend inline fun <T> safeCall(action: suspend () -> T): Result<T> {
    return try {
        Result.Success(action())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
