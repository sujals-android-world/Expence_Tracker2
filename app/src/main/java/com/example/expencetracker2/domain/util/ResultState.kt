package com.example.expencetracker2.domain.util

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error<T>(val exception: String) : ResultState<T>()
    object Loading : ResultState<Nothing>()
}