package com.example.tadaassignment.domain.usecase

import java.io.IOException

fun userFacingError(error: Throwable, isOnline: Boolean, fallback: String): String {
    if (!isOnline || error is IOException) return "No internet connection"
    return fallback
}
