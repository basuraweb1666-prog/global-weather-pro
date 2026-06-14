package com.example.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

data class WeatherInfo(
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val isRaining: Boolean = false,
    val isStorm: Boolean = false
)

object WeatherUtils {

    fun getWeatherInfo(code: Int): WeatherInfo {
        return when (code) {
            0 -> WeatherInfo("Despejado", Icons.Filled.WbSunny, SunGold)
            1 -> WeatherInfo("Mayormente Despejado", Icons.Filled.WbCloudy, BlueSecondary)
            2 -> WeatherInfo("Parcialmente Nublado", Icons.Filled.CloudQueue, TextSecondaryDark)
            3 -> WeatherInfo("Cubierto", Icons.Filled.Cloud, TextSecondaryDark)
            45, 48 -> WeatherInfo("Niebla", Icons.Filled.FilterDrama, TextSecondaryDark)
            51, 53, 55 -> WeatherInfo("Llovizna Ligera", Icons.Filled.Grain, RainCyan, isRaining = true)
            56, 57 -> WeatherInfo("Llovizna Gélida", Icons.Filled.AcUnit, SnowWhite)
            61, 63, 65 -> WeatherInfo("Lluvia", Icons.Filled.WaterDrop, RainCyan, isRaining = true)
            66, 67 -> WeatherInfo("Lluvia Congelante", Icons.Filled.AcUnit, SnowWhite, isRaining = true)
            71, 73, 75 -> WeatherInfo("Nieve", Icons.Filled.AcUnit, SnowWhite)
            77 -> WeatherInfo("Granos de Nieve", Icons.Filled.AcUnit, SnowWhite)
            80, 81, 82 -> WeatherInfo("Chubascos Lluvia", Icons.Filled.Umbrella, RainCyan, isRaining = true)
            85, 86 -> WeatherInfo("Chubascos Nieve", Icons.Filled.SevereCold, SnowWhite)
            95 -> WeatherInfo("Tormenta Eléctrica", Icons.Filled.Thunderstorm, ThunderPurple, isStorm = true)
            96, 99 -> WeatherInfo("Tormenta con Granizo", Icons.Filled.Thunderstorm, ThunderPurple, isStorm = true)
            else -> WeatherInfo("Clima Estable", Icons.Filled.WbSunny, SunGold)
        }
    }

    /**
     * Converts WMO weather codes to a corresponding background color gradient
     */
    fun getWeatherGradient(code: Int): List<Color> {
        return when (code) {
            0, 1 -> listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6)) // Sunny Sky Blue
            2, 3 -> listOf(Color(0xFF1E293B), Color(0xFF475569)) // Cloudy Slate
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> listOf(Color(0xFF0F172A), Color(0xFF1E293B)) // Rainy dark
            95, 96, 99 -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81)) // Thunder Storm purple
            71, 73, 75, 85, 86 -> listOf(Color(0xFF0F172A), Color(0xFF334155)) // Snowy dark blue
            else -> listOf(Color(0xFF0F172A), Color(0xFF1E3A8A))
        }
    }
}
