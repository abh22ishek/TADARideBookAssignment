package com.example.tadaassignment.domain.model

data class SafeLocation(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val aqi: Int,
    val nickname: String? = null
) {
    /** The nickname if the user set one, otherwise the looked-up address. */
    val displayName: String get() = nickname ?: name

    /** True when both points match through the third decimal place. */
    fun isSamePlaceAs(other: SafeLocation): Boolean =
        coordinateBucket(lat) == coordinateBucket(other.lat) &&
            coordinateBucket(lng) == coordinateBucket(other.lng)
}

/**
 * Truncates a coordinate to 3 decimal places. A tiny bias keeps a value that
 * landed just under an exact thousandth, because of floating point, in the
 * right bucket.
 */
fun coordinateBucket(value: Double): Int {
    val scaled = value * 1000.0
    val adjusted = if (scaled >= 0) scaled + 1e-6 else scaled - 1e-6
    return adjusted.toInt()
}
