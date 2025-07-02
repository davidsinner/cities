package com.davidsinner.candidate.cleanarquitecture.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.paging.PagingData
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityDetailScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityListScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.cityscreen.MapScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.state.CityListState
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.typeOf


@Composable
fun AppNavigation(
    navController: NavHostController,
    cityListUiState: CityListState,
    citiesPagingDataFlow: Flow<PagingData<City>>,
    onSearchQueryChanged: (String) -> Unit,
    onToggleFavoriteFilter: () -> Unit,
    onToggleCityFavorite: (City) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = CityListDestination,
        modifier = modifier
    ) {
        composable<CityListDestination> {
            CityListScreen(
                uiState = cityListUiState,
                citiesPagingDataFlow = citiesPagingDataFlow,
                onSearchQueryChanged = onSearchQueryChanged,
                onToggleFavoriteFilter = onToggleFavoriteFilter,
                onToggleCityFavorite = onToggleCityFavorite,
                onNavigateToMap = { city ->
                    navController.navigate(MapDestination(city))
                },
                onNavigateToInfo = { city ->
                    navController.navigate(CityDetailDestination(city))
                }
            )
        }

        composable<CityDetailDestination>(
            typeMap = mapOf(typeOf<City>() to CustomNavType.CityType)
        ) {
            val arguments = it.toRoute<CityDetailDestination>()
            CityDetailScreen(
                city = arguments.city,
                onBack = { navController.popBackStack() }
            )
        }

        composable<MapDestination>(
            typeMap = mapOf(typeOf<City>() to CustomNavType.CityType)
        ) {
            val arguments = it.toRoute<MapDestination>()
            MapScreen(
                latitude = arguments.city.coord.latitude,
                longitude = arguments.city.coord.longitude,
                cityName = arguments.city.name,
                onBack = { navController.popBackStack() }
            )
        }
    }
}