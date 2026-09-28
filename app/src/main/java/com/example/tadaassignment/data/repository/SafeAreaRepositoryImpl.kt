package com.example.tadaassignment.data.repository

import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeLocation
import com.example.tadaassignment.domain.model.coordinateBucket
import com.example.tadaassignment.domain.repository.SafeAreaRepository
import com.example.tadaassignment.data.remote.SafeAreaApiService
import com.example.tadaassignment.data.remote.aqicn.AqicnApiService
import com.example.tadaassignment.data.remote.aqicn.aqiInt
import com.example.tadaassignment.data.remote.aqicn.isNear
import com.example.tadaassignment.data.remote.dto.toDomain
import com.example.tadaassignment.data.remote.geocode.GeocodeApiService
import com.example.tadaassignment.data.remote.mock.BookRequest
import com.example.tadaassignment.data.remote.mock.buildAddressName
import com.example.tadaassignment.data.remote.mock.toBookLocation
import com.example.tadaassignment.data.remote.mock.toDomain
import com.example.tadaassignment.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SafeAreaRepositoryImpl @Inject constructor(
    private val apiService: SafeAreaApiService,
    private val aqicnApiService: AqicnApiService,
    private val geocodeApiService: GeocodeApiService
) : SafeAreaRepository {

    // Caches a lookup by coordinates truncated to 3 decimal places, so two
    // taps in the same neighborhood ("same up to the 3rd decimal") reuse the
    // first result instead of hitting the network again. Entries expire after
    // [AREA_CACHE_TTL_MS] since AQI is time-varying - without a TTL, the first
    // reading for a spot would otherwise be shown for the rest of the process.
    private val areaCache = ConcurrentHashMap<Pair<Int, Int>, CacheEntry>()

    // Only places the user confirmed. Map pans stay in [areaCache] and do not
    // show up in the saved-locations list.
    private val savedLocations = ConcurrentHashMap<Pair<Int, Int>, SafeLocation>()

    private fun cacheKey(lat: Double, lng: Double): Pair<Int, Int> =
        coordinateBucket(lat) to coordinateBucket(lng)

    override suspend fun getAreaAt(lat: Double, lng: Double): SafeLocation =
        withContext(Dispatchers.IO) {
            val key = cacheKey(lat, lng)
            val cached = areaCache[key]
            if (cached != null && !cached.isExpired()) return@withContext cached.location

            // Run all three lookups in parallel instead of one after the
            // other, so a slow/offline call to either real API doesn't add
            // to the total wait.
            val locationDeferred = async { apiService.getAreaAt(lat, lng).toDomain() }
            val liveAqiDeferred = async { fetchLiveAqi(lat, lng) }
            val liveNameDeferred = async { fetchLiveAddressName(lat, lng) }
            val location = locationDeferred.await()
            val liveAqi = liveAqiDeferred.await()
            val liveName = liveNameDeferred.await()
            // Falls back to the mocked AQI/name already on `location` if the
            // real AQICN/BigDataCloud calls fail or have nothing for this spot.
            // A nickname already saved for this spot survives a re-lookup too.
            val resolved = location.copy(
                aqi = liveAqi ?: location.aqi,
                name = liveName ?: location.name,
                nickname = savedLocations[key]?.nickname
            )

            areaCache[key] = CacheEntry(resolved)
            resolved
        }

    override fun getCachedLocations(): List<SafeLocation> = savedLocations.values.toList()

    /**
     * Remembers a place the user chose with Set A / Set B. A nickname already
     * saved for this spot is preserved rather than wiped by a bare re-pick -
     * only [updateCachedLocation] can change or clear a nickname.
     */
    override fun saveLocation(location: SafeLocation): SafeLocation {
        val key = cacheKey(location.lat, location.lng)
        val toSave = location.copy(nickname = location.nickname ?: savedLocations[key]?.nickname)
        savedLocations[key] = toSave
        areaCache[key] = CacheEntry(toSave)
        return toSave
    }

    /** Writes a location (e.g. after a nickname edit) back into the cache by its coordinates. */
    override fun updateCachedLocation(location: SafeLocation) {
        val key = cacheKey(location.lat, location.lng)
        savedLocations[key] = location
        areaCache[key] = CacheEntry(location)
    }

    private suspend fun fetchLiveAqi(lat: Double, lng: Double): Int? = runCatching {
        val token = BuildConfig.AQICN_TOKEN
        if (token.isBlank()) return@runCatching null
        val response = aqicnApiService.getGeoFeed(coords = "$lat;$lng", token = token)
        if (response.status != "ok") return@runCatching null
        val data = response.data ?: return@runCatching null
        // The public "demo" token always returns Shanghai, so a far-away
        // station is ignored and the pin keeps a local mock AQI instead.
        if (!data.isNear(lat, lng)) return@runCatching null
        data.aqiInt()
    }.getOrNull()

    private suspend fun fetchLiveAddressName(lat: Double, lng: Double): String? = runCatching {
        val administrative = geocodeApiService.reverseGeocode(lat, lng).localityInfo?.administrative
        if (administrative.isNullOrEmpty()) null else buildAddressName(administrative)
    }.getOrNull()

    override suspend fun bookRoute(locationA: SafeLocation, locationB: SafeLocation): BookingResult =
        withContext(Dispatchers.IO) {
            val request = BookRequest(locationA.toBookLocation(), locationB.toBookLocation())
            apiService.bookRoute(request).toDomain()
        }

    override suspend fun getBooks(year: Int, month: Int): List<BookingResult> =
        withContext(Dispatchers.IO) {
            apiService.getBooks(year, month).map { it.toDomain() }
        }

    private class CacheEntry(val location: SafeLocation, private val fetchedAtMs: Long = System.currentTimeMillis()) {
        fun isExpired(): Boolean = System.currentTimeMillis() - fetchedAtMs >= AREA_CACHE_TTL_MS
    }

    private companion object {
        val AREA_CACHE_TTL_MS = TimeUnit.MINUTES.toMillis(5)
    }
}
