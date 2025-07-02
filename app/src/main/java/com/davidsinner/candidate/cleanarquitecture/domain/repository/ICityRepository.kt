package com.davidsinner.candidate.cleanarquitecture.domain.repository

import androidx.paging.PagingData
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import kotlinx.coroutines.flow.Flow


interface ICityRepository {
    fun getCities(forceRefresh: Boolean = false): Flow<PagingData<City>>

    fun searchCities(query: String): Flow<PagingData<City>>

    fun getFavoriteCities(): Flow<PagingData<City>>

    suspend fun updateCityFavoriteStatus(city: City, isFavorite: Boolean)
    fun getCityById(cityId: Int): Flow<City?>

    suspend fun refreshAndCacheCities()
}