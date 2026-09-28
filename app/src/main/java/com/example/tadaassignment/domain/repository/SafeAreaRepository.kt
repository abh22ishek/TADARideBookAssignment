package com.example.tadaassignment.domain.repository

import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeLocation

interface SafeAreaRepository {

    suspend fun getAreaAt(lat: Double, lng: Double): SafeLocation

    /** Places the user has actually saved, for the A/B picker. Not every map pan. */
    fun getCachedLocations(): List<SafeLocation>

    /** Remembers a place the user chose with Set A / Set B. */
    fun saveLocation(location: SafeLocation): SafeLocation

    /** Writes a location (e.g. after a nickname edit) back into the cache by its coordinates. */
    fun updateCachedLocation(location: SafeLocation)

    suspend fun bookRoute(locationA: SafeLocation, locationB: SafeLocation): BookingResult

    suspend fun getBooks(year: Int, month: Int): List<BookingResult>
}
