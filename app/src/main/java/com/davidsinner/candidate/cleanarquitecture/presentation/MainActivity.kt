package com.davidsinner.candidate.cleanarquitecture.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.presentation.navigation.AppNavigation
import com.davidsinner.candidate.cleanarquitecture.presentation.navigation.CityDetailDestination
import com.davidsinner.candidate.cleanarquitecture.presentation.navigation.CityListDestination
import com.davidsinner.candidate.cleanarquitecture.presentation.navigation.MapDestination
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityDetailScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityListScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.DetailViewType
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.cityscreen.MapScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.util.OrientationScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.util.rememberWindowOrientation
import com.davidsinner.candidate.cleanarquitecture.presentation.viewmodel.CityViewModel
import com.davidsinner.candidate.cleanarquitecture.theme.CandidateTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val cityViewModel: CityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CandidateTheme {
                val navController = rememberNavController()

                val cityListUiState by cityViewModel.uiState.collectAsState()
                val citiesPagingDataFlow = cityViewModel.citiesPagingData

                val orientation = rememberWindowOrientation()

                var currentDetail: Pair<City, DetailViewType>? by remember { mutableStateOf(null) }

                val currentBackStackEntry by navController.currentBackStackEntryAsState()

                DisposableEffect(currentBackStackEntry) {
                    currentBackStackEntry?.let { backStackEntry ->
                        try {
                            when (backStackEntry.toRoute<Any>()) {
                                is MapDestination -> {
                                    val mapDestination = backStackEntry.toRoute<MapDestination>()
                                    currentDetail = Pair(mapDestination.city, DetailViewType.MAP)
                                }

                                is CityDetailDestination -> {
                                    val cityDetailDestination =
                                        backStackEntry.toRoute<CityDetailDestination>()
                                    currentDetail =
                                        Pair(cityDetailDestination.city, DetailViewType.INFO)
                                }

                                is CityListDestination -> {
                                    currentDetail = null
                                }

                                else -> {
                                    currentDetail = null
                                }
                            }
                        } catch (e: Exception) {
                            currentDetail = null
                        }
                    } ?: run {
                        currentDetail = null
                    }
                    onDispose { }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (orientation == OrientationScreen.Landscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Column(modifier = Modifier.weight(0.5f)) {
                                CityListScreen(
                                    uiState = cityListUiState,
                                    citiesPagingDataFlow = citiesPagingDataFlow,
                                    onSearchQueryChanged = cityViewModel::onSearchQueryChanged,
                                    onToggleFavoriteFilter = cityViewModel::toggleFavoriteFilter,
                                    onToggleCityFavorite = cityViewModel::toggleCityFavorite,
                                    onNavigateToMap = { city ->
                                        currentDetail = Pair(city, DetailViewType.MAP)
                                    },
                                    onNavigateToInfo = { city ->
                                        currentDetail = Pair(city, DetailViewType.INFO)
                                    }
                                )
                            }
                            Column(modifier = Modifier.weight(0.5f)) {
                                currentDetail?.let { (city, viewType) ->
                                    when (viewType) {
                                        DetailViewType.MAP -> {
                                            MapScreen(
                                                latitude = city.coord.latitude,
                                                longitude = city.coord.longitude,
                                                cityName = city.name,
                                                onBack = { currentDetail = null }
                                            )
                                        }

                                        DetailViewType.INFO -> {
                                            CityDetailScreen(
                                                city = city,
                                                onBack = { currentDetail = null }
                                            )
                                        }
                                    }
                                } ?: run {
                                    Text(
                                        text = "Selecciona una ciudad para ver los detalles.",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        AppNavigation(
                            navController = navController,
                            cityListUiState = cityListUiState,
                            citiesPagingDataFlow = citiesPagingDataFlow,
                            onSearchQueryChanged = cityViewModel::onSearchQueryChanged,
                            onToggleFavoriteFilter = cityViewModel::toggleFavoriteFilter,
                            onToggleCityFavorite = cityViewModel::toggleCityFavorite,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
