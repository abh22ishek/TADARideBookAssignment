package com.example.tadaassignment.data.remote

import com.example.tadaassignment.data.remote.dto.LocationDto
import com.example.tadaassignment.data.remote.mock.BookRequest
import com.example.tadaassignment.data.remote.mock.BookResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SafeAreaApiService {

    @GET("area")
    suspend fun getAreaAt(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): LocationDto

    @GET("books")
    suspend fun getBooks(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): List<BookResponse>

    @POST("books")
    suspend fun bookRoute(@Body request: BookRequest): BookResponse
}
