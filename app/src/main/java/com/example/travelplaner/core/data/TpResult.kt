package com.example.travelplaner.core.data

sealed class TpResult<out T> {
    data class Success<out T>(val data: T) : TpResult<T>()
    data class Failure(val error: Throwable) : TpResult<Nothing>()
}