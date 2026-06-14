package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.api.WeatherApiService
import com.example.data.local.WeatherDatabase
import com.example.data.repository.WeatherRepository
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class WeatherApplication : Application() {

    lateinit var database: WeatherDatabase
        private set

    lateinit var repository: WeatherRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize DB
        database = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            "global_weather_pro_db"
        ).fallbackToDestructiveMigration() // safe migration strategy for development
         .build()

        // 2. Initialize Retrofit ApiService using Moshi
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        val apiService = retrofit.create(WeatherApiService::class.java)

        // 3. Initialize Repository
        repository = WeatherRepository(
            apiService = apiService,
            weatherDao = database.weatherDao()
        )
    }
}
