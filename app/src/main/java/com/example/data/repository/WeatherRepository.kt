package com.example.data.repository

import com.example.data.api.GeocodingCity
import com.example.data.api.WeatherApiService
import com.example.data.api.WeatherResponse
import com.example.data.local.FavoriteCity
import com.example.data.local.WeatherCache
import com.example.data.local.WeatherDao
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val weatherAdapter = moshi.adapter(WeatherResponse::class.java)

    val favorites: Flow<List<FavoriteCity>> = weatherDao.getAllFavorites()

    /**
     * Obtains the weather for a given location (lat, lon).
     * If forceRefresh is false, it checks if a valid cache (less than 15 min old) exists.
     * Otherwise it queries the network and updates the local cache on success.
     */
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        forceRefresh: Boolean = false
    ): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        val latLonKey = "${latitude},${longitude}"
        val cached = weatherDao.getCachedWeather(latLonKey)

        if (!forceRefresh && cached != null && (System.currentTimeMillis() - cached.lastUpdated < 15 * 60 * 1000)) {
            val response = deserializeWeather(cached.weatherJson)
            if (response != null) {
                return@withContext Result.success(response)
            }
        }

        try {
            val networkResult = apiService.getWeather(latitude = latitude, longitude = longitude)
            val json = serializeWeather(networkResult)
            weatherDao.insertCache(WeatherCache(latLonKey = latLonKey, weatherJson = json))
            Result.success(networkResult)
        } catch (e: Exception) {
            // Network fallback to any cache available if request fails
            if (cached != null) {
                val response = deserializeWeather(cached.weatherJson)
                if (response != null) {
                    return@withContext Result.success(response)
                }
            }
            Result.failure(e)
        }
    }

    suspend fun searchCities(query: String): List<GeocodingCity> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val response = apiService.searchCities(name = query)
            response.results ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addFavorite(city: FavoriteCity) = withContext(Dispatchers.IO) {
        weatherDao.insertFavorite(city)
    }

    suspend fun removeFavorite(city: FavoriteCity) = withContext(Dispatchers.IO) {
        weatherDao.deleteFavorite(city)
    }

    suspend fun isFavorite(cityId: Long): Boolean = withContext(Dispatchers.IO) {
        weatherDao.isFavorite(cityId)
    }

    private fun serializeWeather(response: WeatherResponse): String {
        return weatherAdapter.toJson(response)
    }

    private fun deserializeWeather(json: String): WeatherResponse? {
        return try {
            weatherAdapter.fromJson(json)
        } catch (e: Exception) {
            null
        }
    }
}
