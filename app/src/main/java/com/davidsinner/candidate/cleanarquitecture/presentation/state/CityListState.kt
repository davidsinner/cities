package com.davidsinner.candidate.cleanarquitecture.presentation.state

import com.davidsinner.candidate.cleanarquitecture.domain.model.City

data class CityListState(
    val cities: List<City> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterFavoritesOnly: Boolean = false,
    val searchQuery: String = ""
)