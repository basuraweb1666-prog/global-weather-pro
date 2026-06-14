package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    // Favorites
    @Query("SELECT * FROM favorite_cities ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(city: FavoriteCity)

    @Delete
    suspend fun deleteFavorite(city: FavoriteCity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = :cityId)")
    suspend fun isFavorite(cityId: Long): Boolean

    // Cache
    @Query("SELECT * FROM weather_cache WHERE latLonKey = :key LIMIT 1")
    suspend fun getCachedWeather(key: String): WeatherCache?

    @Query("SELECT * FROM weather_cache WHERE latLonKey = :key LIMIT 1")
    fun getCachedWeatherFlow(key: String): Flow<WeatherCache?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: WeatherCache)

    @Query("DELETE FROM weather_cache WHERE latLonKey = :key")
    suspend fun deleteCache(key: String)
}
