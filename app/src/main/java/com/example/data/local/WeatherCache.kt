package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCache(
    @PrimaryKey val latLonKey: String, // format: "latitude,longitude"
    val weatherJson: String,           // serialized WeatherResponse
    val lastUpdated: Long = System.currentTimeMillis()
)
