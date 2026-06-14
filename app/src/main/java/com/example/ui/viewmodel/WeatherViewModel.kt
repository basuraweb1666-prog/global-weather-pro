package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeocodingCity
import com.example.data.api.WeatherResponse
import com.example.data.local.FavoriteCity
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    object Idle : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val response: WeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

data class WeatherAlert(
    val title: String,
    val description: String,
    val severity: String, // "Crítica", "Alerta", "Aviso"
    val cityName: String,
    val category: String, // "Calor", "Lluvia", "Viento", "UV"
    val timestamp: Long = System.currentTimeMillis()
)

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _selectedCity = MutableStateFlow<GeocodingCity?>(null)
    val selectedCity: StateFlow<GeocodingCity?> = _selectedCity.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeocodingCity>>(emptyList())
    val searchResults: StateFlow<List<GeocodingCity>> = _searchResults.asStateFlow()

    private val _isCurrentCityFavorite = MutableStateFlow(false)
    val isCurrentCityFavorite: StateFlow<Boolean> = _isCurrentCityFavorite.asStateFlow()

    val favorites: StateFlow<List<FavoriteCity>> = repository.favorites
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts.asStateFlow()

    // Flag for active radar animation frame
    private val _radarFrame = MutableStateFlow(0)
    val radarFrame: StateFlow<Int> = _radarFrame.asStateFlow()

    init {
        // Debounced search query execution
        setupSearchDebounce()

        // Sync favorite state when selected city changes
        viewModelScope.launch {
            _selectedCity.collect { city ->
                if (city != null) {
                    _isCurrentCityFavorite.value = repository.isFavorite(city.id)
                } else {
                    _isCurrentCityFavorite.value = false
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .filter { it.isNotBlank() && it.length >= 2 }
                .distinctUntilChanged()
                .collect { query ->
                    val results = repository.searchCities(query)
                    _searchResults.value = results
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        }
    }

    fun selectCity(city: GeocodingCity) {
        _selectedCity.value = city
        fetchWeatherForCoordinates(city.latitude, city.longitude, city.name)
    }

    fun selectFavoriteCity(city: FavoriteCity) {
        val mappedCity = GeocodingCity(
            id = city.id,
            name = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
            country = city.country,
            country_code = null,
            admin1 = city.admin1,
            timezone = null
        )
        _selectedCity.value = mappedCity
        fetchWeatherForCoordinates(city.latitude, city.longitude, city.name)
    }

    /**
     * Re-fetch current selection weather, forcing API download
     */
    fun refreshCurrentWeather() {
        val city = _selectedCity.value
        if (city != null) {
            fetchWeatherForCoordinates(city.latitude, city.longitude, city.name, forceRefresh = true)
        }
    }

    fun fetchWeatherForCoordinates(
        lat: Double,
        lon: Double,
        cityName: String,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            val result = repository.getWeather(lat, lon, forceRefresh)
            result.onSuccess { response ->
                _weatherState.value = WeatherUiState.Success(response)
                generateDynamicAlertas(response, cityName)
            }
            result.onFailure { error ->
                _weatherState.value = WeatherUiState.Error(
                    error.localizedMessage ?: "Error desconocido de conexión"
                )
            }
        }
    }

    fun toggleFavoriteCurrentCity() {
        val city = _selectedCity.value ?: return
        viewModelScope.launch {
            val isFav = repository.isFavorite(city.id)
            if (isFav) {
                repository.removeFavorite(
                    FavoriteCity(
                        id = city.id,
                        name = city.name,
                        latitude = city.latitude,
                        longitude = city.longitude,
                        country = city.country,
                        admin1 = city.admin1
                    )
                )
                _isCurrentCityFavorite.value = false
            } else {
                repository.addFavorite(
                    FavoriteCity(
                        id = city.id,
                        name = city.name,
                        latitude = city.latitude,
                        longitude = city.longitude,
                        country = city.country,
                        admin1 = city.admin1
                    )
                )
                _isCurrentCityFavorite.value = true
            }
        }
    }

    fun removeFavoriteCity(city: FavoriteCity) {
        viewModelScope.launch {
            repository.removeFavorite(city)
            if (_selectedCity.value?.id == city.id) {
                _isCurrentCityFavorite.value = false
            }
        }
    }

    /**
     * Generates realistic warnings and notifications dynamically driven by the retrieved weather characteristics
     */
    private fun generateDynamicAlertas(response: WeatherResponse, cityName: String) {
        val current = response.current ?: return
        val daily = response.daily
        val activeAlerts = mutableListOf<WeatherAlert>()

        // 1. Wind alert
        val wind = current.wind_speed_10m ?: 0.0
        if (wind > 35.0) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Vientos Huracanados",
                    description = "Se registran rachas de viento fuertes de %.1f km/h. Asegure objetos sueltos.".format(wind),
                    severity = "Crítica",
                    cityName = cityName,
                    category = "Viento"
                )
            )
        } else if (wind > 20.0) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Viento Moderado",
                    description = "Rachas de %.1f km/h que podrían agitar copas de árboles.".format(wind),
                    severity = "Aviso",
                    cityName = cityName,
                    category = "Viento"
                )
            )
        }

        // 2. Heat alert or cold warning
        val temp = current.temperature_2m
        if (temp > 38.0) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Ola de Calor Extremo",
                    description = "Alerta por altas temperaturas extremas de %.1f°C. Manténgase hidratado y bajo sombra.".format(temp),
                    severity = "Crítica",
                    cityName = cityName,
                    category = "Calor"
                )
            )
        } else if (temp > 32.0) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Calor Elevado",
                    description = "La temperatura alcanza %.1f°C. Se aconseja precaución ante exposición al sol prolongada.".format(temp),
                    severity = "Aviso",
                    cityName = cityName,
                    category = "Calor"
                )
            )
        } else if (temp < 3.0) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Helada y Clima Gélido",
                    description = "Temperatura de %.1f°C cercana al punto de congelación. Riesgo de escarcha en calzadas.".format(temp),
                    severity = "Alerta",
                    cityName = cityName,
                    category = "Calor"
                )
            )
        }

        // 3. Precipitations & Thunderstorms
        val code = current.weather_code
        if (code in listOf(95, 96, 99)) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Tormenta Activa",
                    description = "Tormenta eléctrica reportándose en la zona con posibilidad de descargas directas y granizo.",
                    severity = "Crítica",
                    cityName = cityName,
                    category = "Lluvia"
                )
            )
        } else if (code in listOf(63, 65, 81, 82)) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Precipitaciones Intensas",
                    description = "Lluvias intensas o chubascos continuos acumulándose. Conduzca con extrema precaución.",
                    severity = "Alerta",
                    cityName = cityName,
                    category = "Lluvia"
                )
            )
        }

        // 4. UV indexes
        val maxUv = daily?.uv_index_max?.firstOrNull() ?: 0.0
        if (maxUv > 8.0) {
            activeAlerts.add(
                WeatherAlert(
                    title = "Índice UV Extremadamente Alto",
                    description = "Radiación solar máxima esperada de %.1f. Imprescindible bloqueador SPF 50 e hidratación.".format(maxUv),
                    severity = "Alerta",
                    cityName = cityName,
                    category = "UV"
                )
            )
        }

        _alerts.value = activeAlerts
    }

    // Increments radar animation frames loop
    fun updateRadarFrame() {
        _radarFrame.value = (_radarFrame.value + 1) % 6
    }
}

class WeatherViewModelFactory(
    private val repository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
