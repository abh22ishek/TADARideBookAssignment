package com.example.tadaassignment.data.remote.geocode

import com.example.tadaassignment.data.remote.mock.LocalityInfo
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * BigDataCloud's free, no-API-key-required client-side reverse geocoding
 * endpoint: https://www.bigdatacloud.com/reverse-geocoding/reverse-geocode-to-city-api
 */
interface GeocodeApiService {

    @GET("data/reverse-geocode-client")
    suspend fun reverseGeocode(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("localityLanguage") localityLanguage: String = "en"
    ): GeocodeResponse
}

data class GeocodeResponse(
    val localityInfo: LocalityInfo?
)
