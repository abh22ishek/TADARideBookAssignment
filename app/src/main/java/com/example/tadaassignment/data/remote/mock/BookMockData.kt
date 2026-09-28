package com.example.tadaassignment.data.remote.mock

import kotlin.math.roundToInt

data class BookLocation(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val name: String,
    val nickname: String? = null
)

/** Body for `POST /books`. */
data class BookRequest(
    val a: BookLocation,
    val b: BookLocation
)

/** Body returned by `POST /books`. */
data class BookResponse(
    val id: String,
    val a: BookLocation,
    val b: BookLocation,
    val price: Double
)

data class LocalityPlace(
    val order: Int,
    val name: String
)

data class LocalityInfo(
    val administrative: List<LocalityPlace>? = null
)

fun buildAddressName(administrative: List<LocalityPlace>): String =
    administrative
        .sortedByDescending { it.order }
        .take(2)
        .sortedBy { it.order }
        .joinToString(", ") { it.name }

fun estimatePrice(a: BookLocation, b: BookLocation): Double {
    val dLat = a.latitude - b.latitude
    val dLng = a.longitude - b.longitude
    val distanceKm = kotlin.math.sqrt(dLat * dLat + dLng * dLng) * 111.0
    val baseFare = 3.5
    val perKmRate = 1.2
    return ((baseFare + distanceKm * perKmRate) * 100).roundToInt() / 100.0
}

object BookMockData {

    val localityInfo = LocalityInfo(
        administrative = listOf(
            LocalityPlace(order = 2, name = "South Korea"),
            LocalityPlace(order = 3, name = "Seoul"),
            LocalityPlace(order = 4, name = "Seocho District"),
            LocalityPlace(order = 5, name = "Yangjae 2(i)-dong")
        )
    )
}
