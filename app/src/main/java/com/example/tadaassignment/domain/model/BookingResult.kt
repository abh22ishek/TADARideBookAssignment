package com.example.tadaassignment.domain.model

/** Result of `POST /books`, used to render Screen 3. */
data class BookingResult(
    val id: String,
    val locationA: SafeLocation,
    val locationB: SafeLocation,
    val price: Double
)
