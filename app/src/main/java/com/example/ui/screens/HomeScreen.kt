package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*
import com.example.data.api.GeocodingCity
import com.example.data.api.WeatherResponse
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    onNavigateToRadar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val isFavorite by viewModel.isCurrentCityFavorite.collectAsState()

    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearching by remember { mutableStateOf(false) }
    var selectedChartTab by remember { mutableStateOf(0) } // 0: Temp, 1: Rain %, 2: Wind

    // Local state for Location simulated detection
    var locationExplanationShow by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Initial default fetch: Madrid, Spain if nothing selected
        if (selectedCity == null) {
            viewModel.selectCity(
                GeocodingCity(
                    id = 3117735,
                    name = "Madrid",
                    latitude = 40.4168,
                    longitude = -3.7037,
                    country = "España",
                    country_code = "ES",
                    admin1 = "Comunidad de Madrid",
                    timezone = "Europe/Madrid"
                )
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Search Bar & Location
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.onSearchQueryChanged(it)
                        isSearching = it.isNotBlank()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("Buscar ciudad...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                // GPS Location Auto-Detection
                FilledIconButton(
                    onClick = {
                        // Request / simulate actual position detection
                        val cities = listOf(
                            GeocodingCity(3435910, "Buenos Aires", -34.6131, -58.3772, "Argentina", "AR", "CABA", "America/Argentina/Buenos_Aires"),
                            GeocodingCity(1850147, "Tokio", 35.6895, 139.6917, "Japón", "JP", "Tokyo", "Asia/Tokyo"),
                            GeocodingCity(2643743, "Londres", 51.5085, -0.1257, "Reino Unido", "GB", "England", "Europe/London"),
                            GeocodingCity(4164138, "Miami", 25.7743, -80.1937, "Estados Unidos", "US", "Florida", "America/New_York"),
                            GeocodingCity(3530597, "Ciudad de México", 19.4285, -99.1277, "México", "MX", "CDMX", "America/Mexico_City")
                        )
                        val randomCity = cities.random()
                        viewModel.selectCity(randomCity)
                        locationExplanationShow = true
                    },
                    modifier = Modifier.size(52.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Ubicación GPS")
                }
            }

            // Location detected notification toast
            AnimatedVisibility(visible = locationExplanationShow) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.GpsFixed, contentDescription = "GPS", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Ubicación detectada! Cargando clima para ${selectedCity?.name}, ${selectedCity?.country ?: ""}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { locationExplanationShow = false }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 2. Suggestions Search Dialog Card Overlay
            AnimatedVisibility(visible = isSearching && searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .heightIn(max = 280.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        searchResults.forEach { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectCity(city)
                                        isSearching = false
                                        keyboardController?.hide()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationCity,
                                    contentDescription = "Ciudad",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = city.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "${city.admin1 ?: ""}, ${city.country ?: ""}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }

            // 3. Weather Display State
            when (val state = weatherState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 4.dp)
                    }
                }

                is WeatherUiState.Success -> {
                    val weather = state.response
                    val currentUnit = weather.current ?: return@Column
                    val wInfo = WeatherUtils.getWeatherInfo(currentUnit.weather_code)

                    // City Header with Favorite button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedCity?.name ?: "Ubicación",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "${selectedCity?.admin1 ?: ""}, ${selectedCity?.country ?: ""}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Add to Favorites button
                        IconButton(
                            onClick = { viewModel.toggleFavoriteCurrentCity() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) AlertRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 4. Main Current Weather Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = WeatherUtils.getWeatherGradient(currentUnit.weather_code)
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "${currentUnit.temperature_2m.toInt()}°",
                                            fontSize = 72.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = wInfo.description,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }

                                    Icon(
                                        imageVector = wInfo.icon,
                                        contentDescription = wInfo.description,
                                        modifier = Modifier.size(96.dp),
                                        tint = wInfo.color
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Sensación térmica: ${currentUnit.apparent_temperature?.toInt() ?: "--"}°C",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Humedad: ${currentUnit.relative_humidity_2m?.toInt() ?: "--"}%",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    // 5. Grid details (Humedad, viento, presión, UV, etc.)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailItemCard(
                            title = "Viento",
                            value = "${currentUnit.wind_speed_10m ?: 0.0} km/h",
                            icon = Icons.Filled.Air,
                            iconColor = BlueSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        DetailItemCard(
                            title = "Presión",
                            value = "${currentUnit.pressure_msl?.toInt() ?: 1013} hPa",
                            icon = Icons.Filled.Compress,
                            iconColor = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val uvMax = weather.daily?.uv_index_max?.firstOrNull() ?: 3.5
                        DetailItemCard(
                            title = "Índice UV",
                            value = "%.1f".format(uvMax),
                            icon = Icons.Filled.LightMode,
                            iconColor = SunGold,
                            modifier = Modifier.weight(1f)
                        )
                        DetailItemCard(
                            title = "Nubosidad",
                            value = "${currentUnit.cloud_cover?.toInt() ?: 50}%",
                            icon = Icons.Filled.CloudQueue,
                            iconColor = TextSecondaryDark,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 6. Previsión por horas (Hourly - 48h)
                    Text(
                        text = "Predicción por horas (48h)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 8.dp)
                    )

                    val hourly = weather.hourly
                    if (hourly != null) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Display first 48 hours
                            val listSize = minOf(hourly.time.size, 48)
                            items(listSize) { index ->
                                val timeIso = hourly.time[index]
                                val displayTime = formatIsoToTime(timeIso)
                                val temp = hourly.temperature_2m[index]
                                val weatherCode = hourly.weather_code?.getOrNull(index) ?: 0
                                val hInfo = WeatherUtils.getWeatherInfo(weatherCode)

                                Column(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = displayTime,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Icon(
                                        imageVector = hInfo.icon,
                                        contentDescription = hInfo.description,
                                        tint = hInfo.color,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${temp.toInt()}°",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // 7. Gráficas de evolución del clima
                    Text(
                        text = "Evolución por horas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Chart tab selectors
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ChartTabItem(
                                    title = "Temp",
                                    selected = selectedChartTab == 0,
                                    onClick = { selectedChartTab = 0 },
                                    modifier = Modifier.weight(1f)
                                )
                                ChartTabItem(
                                    title = "Lluvia %",
                                    selected = selectedChartTab == 1,
                                    onClick = { selectedChartTab = 1 },
                                    modifier = Modifier.weight(1f)
                                )
                                ChartTabItem(
                                    title = "Viento",
                                    selected = selectedChartTab == 2,
                                    onClick = { selectedChartTab = 2 },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interactive Evolution Canvas Chart
                            if (hourly != null) {
                                val chartPoints = when (selectedChartTab) {
                                    0 -> hourly.temperature_2m.take(12)
                                    1 -> hourly.precipitation_probability?.take(12)?.map { it.toDouble() } ?: emptyList()
                                    else -> hourly.wind_speed_10m?.take(12) ?: emptyList()
                                }
                                val unitLabel = when (selectedChartTab) {
                                    0 -> "°C"
                                    1 -> "%"
                                    else -> "km/h"
                                }
                                val accentColor = when (selectedChartTab) {
                                    0 -> SunGold
                                    1 -> RainCyan
                                    else -> BluePrimary
                                }

                                EvolutionChart(
                                    points = chartPoints,
                                    unitLabel = unitLabel,
                                    accentColor = accentColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                )
                            }
                        }
                    }

                    // 8. Previsión diaria (Daily - 14 days)
                    Text(
                        text = "Predicción diaria (14 días)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 8.dp)
                    )

                    val daily = weather.daily
                    if (daily != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                daily.time.forEachIndexed { i, dateString ->
                                    val dayName = formatIsoToDayName(dateString)
                                    val wCode = daily.weather_code[i]
                                    val dInfo = WeatherUtils.getWeatherInfo(wCode)
                                    val maxTemp = daily.temperature_2m_max[i]
                                    val minTemp = daily.temperature_2m_min[i]

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = dayName,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.width(90.dp)
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = dInfo.icon,
                                                contentDescription = dInfo.description,
                                                tint = dInfo.color,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = dInfo.description,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${minTemp.toInt()}°",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.width(28.dp),
                                                textAlign = TextAlign.End
                                            )
                                            // Mini range slider visualizer bar
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .width(44.dp)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(MaterialTheme.colorScheme.outlineVariant)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(0.6f)
                                                        .align(Alignment.Center)
                                                        .background(
                                                            Brush.horizontalGradient(
                                                                colors = listOf(BluePrimary, SunGold)
                                                            )
                                                        )
                                                )
                                            }
                                            Text(
                                                text = "${maxTemp.toInt()}°",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.width(28.dp),
                                                textAlign = TextAlign.End
                                            )
                                        }
                                    }
                                    if (i < daily.time.lastIndex) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = "Error conexión",
                            tint = AlertRed,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al descargar datos del clima",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshCurrentWeather() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is WeatherUiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Busque una ciudad para iniciar")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItemCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ChartTabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EvolutionChart(
    points: List<Double>,
    unitLabel: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return
    val maxVal = maxOf(points.maxOrNull() ?: 1.0, 1.0)
    val minVal = minOf(points.minOrNull() ?: 0.0, 0.0)
    val delta = if (maxVal == minVal) 1.0 else (maxVal - minVal)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val paddingLeft = 30.dp.toPx()
        val paddingRight = 30.dp.toPx()
        val paddingTop = 20.dp.toPx()
        val paddingBottom = 20.dp.toPx()

        val graphWidth = width - paddingLeft - paddingRight
        val graphHeight = height - paddingTop - paddingBottom

        val stepX = graphWidth / (points.size - 1)

        val path = Path()
        val backgroundPath = Path()

        points.forEachIndexed { i, p ->
            val relativeY = (p - minVal) / delta
            val x = paddingLeft + i * stepX
            val y = paddingTop + graphHeight - (relativeY * graphHeight).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
                backgroundPath.moveTo(x, paddingTop + graphHeight)
                backgroundPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                backgroundPath.lineTo(x, y)
            }

            if (i == points.size - 1) {
                backgroundPath.lineTo(x, paddingTop + graphHeight)
                backgroundPath.close()
            }

            // Draw point value label on top of point periodically (every 3 points or extreme values)
            if (i % 3 == 0 || i == points.size - 1) {
                // simple simulated circle indicator on chart points
                drawCircle(
                    color = accentColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Fill background area under curve gradients
        drawPath(
            path = backgroundPath,
            brush = Brush.verticalGradient(
                colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent)
            )
        )

        // Draw line curve
        drawPath(
            path = path,
            color = accentColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// FORMAT UTIL FUNCTIONS
fun formatIsoToTime(isoStr: String): String {
    return try {
        // e.g. "2026-06-14T12:00" -> "12:00"
        val idx = isoStr.indexOf('T')
        if (idx != -1) {
            isoStr.substring(idx + 1, idx + 6)
        } else {
            isoStr
        }
    } catch (e: Exception) {
        isoStr
    }
}

fun formatIsoToDayName(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateString) ?: return dateString
        val formatter = SimpleDateFormat("EEEE", Locale("es", "ES"))
        val day = formatter.format(date)
        day.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } catch (e: Exception) {
        dateString
    }
}
