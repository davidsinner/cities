package com.davidsinner.candidate.cleanarquitecture.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import androidx.paging.compose.items
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.presentation.state.CityListState // Correct type for uiState

@Composable
fun CityListScreen(
    uiState: CityListState,
    citiesPagingDataFlow: Flow<PagingData<City>>,
    onSearchQueryChanged: (String) -> Unit,
    onToggleFavoriteFilter: () -> Unit,
    onToggleCityFavorite: (City) -> Unit,
    onNavigateToMap: (City) -> Unit,
    onNavigateToInfo: (City) -> Unit
) {
    val cities = citiesPagingDataFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Buscar ciudad o país") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.filterFavoritesOnly,
                onCheckedChange = { onToggleFavoriteFilter() },
                modifier = Modifier.testTag("favoriteFilterCheckbox")
            )
            Text("Mostrar solo favoritos")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.error != null) {
            Text(
                text = "Error de la aplicación: ${uiState.error}",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        when (cities.loadState.refresh) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
            is LoadState.Error -> {
                val e = cities.loadState.refresh as LoadState.Error
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Error al cargar ciudades: ${e.error.localizedMessage ?: "Error desconocido"}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                if (cities.itemCount == 0 && cities.loadState.append.endOfPaginationReached) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No se encontraron ciudades.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = cities,
                            key = { city -> city.id }
                        ) { city ->
                            if (city != null) {
                                CityListItem(
                                    city = city,
                                    onToggleCityFavorite = onToggleCityFavorite,
                                    onNavigateToMap = onNavigateToMap,
                                    onNavigateToInfo = onNavigateToInfo
                                )
                            } else {
                                Text(
                                    "Cargando marcador de posición...",
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp)
                                )
                            }
                        }

                        cities.apply {
                            when {
                                loadState.append is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }

                                loadState.append is LoadState.Error -> {
                                    item {
                                        val e = loadState.append as LoadState.Error
                                        Text(
                                            "Error al cargar más: ${e.error.localizedMessage ?: "Error desconocido"}",
                                            color = Color.Red,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}