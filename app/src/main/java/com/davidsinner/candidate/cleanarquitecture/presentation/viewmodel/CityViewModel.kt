package com.davidsinner.candidate.cleanarquitecture.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.domain.repository.ICityRepository
import com.davidsinner.candidate.cleanarquitecture.presentation.state.CityListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityViewModel @Inject constructor(
    private val repository: ICityRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(CityListState())
    val uiState: StateFlow<CityListState> =
        _uiState.asStateFlow()


    val citiesPagingData: Flow<PagingData<City>> =
        _uiState
            .debounce { currentState ->

                if (currentState.searchQuery != _uiState.value.searchQuery) 300L else 0L // Espera 300ms.
            }
            .distinctUntilChanged { old, new ->
                old.searchQuery == new.searchQuery && old.filterFavoritesOnly == new.filterFavoritesOnly
            }
            .flatMapLatest { currentState ->

                when {
                    currentState.filterFavoritesOnly -> repository.getFavoriteCities()
                    currentState.searchQuery.isNotBlank() -> repository.searchCities(currentState.searchQuery)
                    else -> repository.getCities(forceRefresh = false) // `forceRefresh` se maneja con `refreshAndCacheCities`.
                }
            }
            .cachedIn(viewModelScope)

    init {

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            try {
                repository.refreshAndCacheCities()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.localizedMessage ?: "Error inicial al cargar ciudades"
                    )
                }
                println("ERROR ViewModel Init: ${e.message}")
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFavoriteFilter() {
        _uiState.update { it.copy(filterFavoritesOnly = !it.filterFavoritesOnly) }
    }

    fun toggleCityFavorite(city: City) {
        viewModelScope.launch {
            try {
                repository.updateCityFavoriteStatus(city, !city.isFavorite)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cambiar favorito: ${e.localizedMessage ?: "Desconocido"}"
                    )
                }
            }
        }
    }
}
