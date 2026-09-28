package com.example.tadaassignment.data.remote.dto

import com.example.tadaassignment.domain.model.SafeLocation

data class LocationDto(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val aqi: Int,
    val nickname: String?
)

fun LocationDto.toDomain(): SafeLocation = SafeLocation(
    id = id,
    name = name,
    lat = lat,
    lng = lng,
    aqi = aqi,
    nickname = nickname
)
