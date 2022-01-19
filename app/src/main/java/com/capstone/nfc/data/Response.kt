package com.capstone.nfc.data

import com.capstone.nfc.Constants

sealed class Response<out T> {
    object Loading: Response<Nothing>()
    data class Success<out T>(val data: T): Response<T>()
    data class Failure(val errorMessage: String = Constants.DEFAULT_ERROR_MESSAGE): Response<Nothing>()
}