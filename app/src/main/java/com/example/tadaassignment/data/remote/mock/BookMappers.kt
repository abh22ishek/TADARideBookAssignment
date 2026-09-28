package com.example.tadaassignment.data.remote.mock

import com.example.tadaassignment.domain.model.BookingResult
import com.example.tadaassignment.domain.model.SafeLocation

fun SafeLocation.toBookLocation(): BookLocation = BookLocation(
    latitude = lat,
    longitude = lng,
    aqi = aqi,
    name = name,
    nickname = nickname
)

fun BookLocation.toSafeLocation(): SafeLocation = SafeLocation(
    id = "$latitude,$longitude",
    name = name,
    lat = latitude,
    lng = longitude,
    aqi = aqi,
    nickname = nickname
)

fun BookResponse.toDomain(): BookingResult = BookingResult(
    id = id,
    locationA = a.toSafeLocation(),
    locationB = b.toSafeLocation(),
    price = price
)
