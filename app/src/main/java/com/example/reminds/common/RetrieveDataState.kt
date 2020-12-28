package com.example.reminds.common

sealed class RetrieveDataState<out T> {
    object Start : RetrieveDataState<Nothing>()

    data class Success<T>(val data: T) : RetrieveDataState<T>()

    data class Failure(val throwable: Throwable) : RetrieveDataState<Nothing>()

}