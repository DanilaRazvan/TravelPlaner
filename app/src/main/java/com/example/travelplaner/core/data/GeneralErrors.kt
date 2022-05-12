package com.example.travelplaner.core.data

sealed class GeneralErrorResult {
    object NoInternetError: GeneralErrorResult()
    object TimeoutError: GeneralErrorResult()
}

sealed class GeneralException: Throwable() {
    object NoInternetException: GeneralException()
    object SocketTimeoutException: GeneralException()
}