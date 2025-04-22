package com.kioskable.app.data.remote

import kotlinx.coroutines.CoroutineExceptionHandler
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber

class NetworkErrorHandler {
    companion object {
        // Create a CoroutineExceptionHandler for handling exceptions in coroutines
        fun createCoroutineExceptionHandler(
            onError: (String) -> Unit = {}
        ): CoroutineExceptionHandler {
            return CoroutineExceptionHandler { _, throwable ->
                val errorMessage = when (throwable) {
                    is IOException -> "Network error: No internet connection"
                    is HttpException -> {
                        when (throwable.code()) {
                            401 -> "Authentication error: Please log in again"
                            403 -> "You don't have permission to access this resource"
                            404 -> "Resource not found"
                            in 500..599 -> "Server error: Please try again later"
                            else -> "Network error: ${throwable.message()}"
                        }
                    }
                    else -> "Error: ${throwable.message}"
                }
                
                Timber.e(throwable, "Network error: $errorMessage")
                onError(errorMessage)
            }
        }
        
        // Utility function to handle common HTTP exceptions and convert to user-friendly messages
        fun handleHttpException(exception: HttpException): String {
            return when (exception.code()) {
                401 -> "Authentication error: Please log in again"
                403 -> "You don't have permission to access this resource"
                404 -> "Resource not found"
                in 500..599 -> "Server error: Please try again later"
                else -> "Error: ${exception.message()}"
            }
        }
    }
} 