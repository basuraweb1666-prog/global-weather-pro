package com.example.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingCity>?
)

@JsonClass(generateAdapter = true)
data class GeocodingCity(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val country_code: String?,
    val admin1: String?,
    val timezone: String?
)
