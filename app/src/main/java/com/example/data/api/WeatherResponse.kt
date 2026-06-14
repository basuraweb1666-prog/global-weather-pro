package com.example.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val elevation: Double?,
    val current: CurrentWeather?,
    val hourly: HourlyForecast?,
    val daily: DailyForecast?
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    val time: String,
    val temperature_2m: Double,
    val relative_humidity_2m: Double?,
    val apparent_temperature: Double?,
    val is_day: Int?,
    val precipitation: Double?,
    val weather_code: Int,
    val cloud_cover: Double?,
    val pressure_msl: Double?,
    val wind_speed_10m: Double?
)

@JsonClass(generateAdapter = true)
data class HourlyForecast(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val relative_humidity_2m: List<Double>?,
    val apparent_temperature: List<Double>?,
    val precipitation_probability: List<Int>?,
    val weather_code: List<Int>?,
    val wind_speed_10m: List<Double>?,
    val uv_index: List<Double>?
)

@JsonClass(generateAdapter = true)
data class DailyForecast(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val apparent_temperature_max: List<Double>?,
    val apparent_temperature_min: List<Double>?,
    val sunrise: List<String>?,
    val sunset: List<String>?,
    val uv_index_max: List<Double>?,
    val precipitation_sum: List<Double>?,
    val precipitation_probability_max: List<Int>?,
    val wind_speed_10m_max: List<Double>?
)
