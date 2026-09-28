package com.example.tadaassignment.data.remote.aqicn

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** https://aqicn.org/json-api/doc/#api-Geolocalized_Feed-GetGeolocFeed */
interface AqicnApiService {

    @GET("feed/geo:{coords}/")
    suspend fun getGeoFeed(
        @Path("coords") coords: String,
        @Query("token") token: String
    ): AqicnResponse
}

data class AqicnResponse(
    val status: String,
    val data: AqicnData?
)

data class AqicnData(
    // Number (82) or "-" when the station has no reading.
    val aqi: JsonElement?,
    val city: AqicnCity?
)

data class AqicnCity(
    val geo: List<Double>?
)

fun AqicnData.aqiInt(): Int? {
    val value = aqi ?: return null
    if (!value.isJsonPrimitive) return null
    val primitive = value.asJsonPrimitive
    return when {
        primitive.isNumber -> primitive.asInt
        primitive.isString -> primitive.asString.toIntOrNull()
        else -> null
    }
}

/** True when the station is close enough to treat as this pin's air quality. */
fun AqicnData.isNear(lat: Double, lng: Double, maxKm: Double = 100.0): Boolean {
    val geo = city?.geo ?: return false
    if (geo.size < 2) return false
    return distanceKm(lat, lng, geo[0], geo[1]) <= maxKm
}

private fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2.0) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2.0)
    return 2 * earthKm * asin(min(1.0, sqrt(a)))
}

