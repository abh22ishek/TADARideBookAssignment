package com.example.tadaassignment.data.remote.mock

import com.example.tadaassignment.domain.model.coordinateBucket
import com.example.tadaassignment.data.remote.dto.LocationDto
import kotlin.math.abs

/**
 * Fallback pin data when live geocode/AQI is unavailable. Names are
 * coordinate-based so they are not tied to Singapore (or any other city).
 */
object MockDataSource {

    const val DEFAULT_LAT = 1.3163
    const val DEFAULT_LNG = 103.8837

    fun areaAt(lat: Double, lng: Double): LocationDto {
        val latBucket = coordinateBucket(lat)
        val lngBucket = coordinateBucket(lng)
        val pseudoAqi = (abs(latBucket + lngBucket) % 100)
        return LocationDto(
            id = "loc-$latBucket-$lngBucket",
            name = fallbackName(lat, lng),
            lat = lat,
            lng = lng,
            aqi = pseudoAqi,
            nickname = null
        )
    }

    private fun fallbackName(lat: Double, lng: Double): String =
        String.format(java.util.Locale.US, "%.3f, %.3f", lat, lng)
}
