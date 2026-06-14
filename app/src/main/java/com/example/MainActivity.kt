package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.RadarScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WeatherViewModel
import com.example.ui.viewmodel.WeatherViewModelFactory

class MainActivity : ComponentActivity() {

    // Initialize Constructor Injected ViewModel through lightweight Service Locator application container
    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory((application as WeatherApplication).repository)
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mandatory edge to edge full bleed layout support
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var currentTab by remember { mutableStateOf(0) } // 0: Pronostico, 1: Radar, 2: Favoritos, 3: Alertas

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                label = { Text("Pronóstico") },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 0) Icons.Filled.Cloud else Icons.Filled.CloudQueue,
                                        contentDescription = "Pronóstico"
                                    )
                                }
                            )

                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                label = { Text("Radar") },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 1) Icons.Filled.Map else Icons.Filled.Map,
                                        contentDescription = "Radar"
                                    )
                                }
                            )

                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                label = { Text("Favoritos") },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 2) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favoritos"
                                    )
                                }
                            )

                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { currentTab = 3 },
                                label = { Text("Alertas") },
                                icon = {
                                    Icon(
                                        imageVector = if (currentTab == 3) Icons.Filled.Warning else Icons.Filled.Warning,
                                        contentDescription = "Alertas"
                                    )
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    // Animated transition tab switching under 300ms
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(180))
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        label = "TabTransition"
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToRadar = { currentTab = 1 }
                            )
                            1 -> RadarScreen()
                            2 -> FavoritesScreen(
                                viewModel = viewModel,
                                onFavoriteCitySelected = { currentTab = 0 } // jump to forecast details on click
                            )
                            3 -> AlertsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
