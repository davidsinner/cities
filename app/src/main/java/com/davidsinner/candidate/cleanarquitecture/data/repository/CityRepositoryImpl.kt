package com.davidsinner.candidate.cleanarquitecture.data.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.davidsinner.candidate.cleanarquitecture.data.CityApiService
import com.davidsinner.candidate.cleanarquitecture.data.room.CityDao
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.domain.repository.ICityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CityRepositoryImpl @Inject constructor(
    private val apiService: CityApiService,
    private val cityDao: CityDao
) : ICityRepository {

    private val PAGING_PAGE_SIZE = 20

    override suspend fun refreshAndCacheCities() {
        withContext(Dispatchers.IO) {
            try {
                val fetchedCities = apiService.getCities()

                if (fetchedCities.isNotEmpty()) {
                    cityDao.clearAllCities()
                    cityDao.insertCities(fetchedCities)
                    println("DEBUG: Ciudades fetched and inserted: ${fetchedCities.size}")
                } else {
                    println("DEBUG: No cities fetched from API.")
                }
            } catch (e: Exception) {
                println("ERROR: Failed to fetch and cache cities: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun getCities(forceRefresh: Boolean): Flow<PagingData<City>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                cityDao.getCitiesPaged()
            }
        ).flow.flowOn(Dispatchers.IO)
    }

    override fun searchCities(query: String): Flow<PagingData<City>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                cityDao.searchCitiesPaged(query)
            }
        ).flow.flowOn(Dispatchers.IO)
    }

    override fun getFavoriteCities(): Flow<PagingData<City>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                cityDao.getFavoriteCitiesPaged()
            }
        ).flow.flowOn(Dispatchers.IO)
    }

    override suspend fun updateCityFavoriteStatus(city: City, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            val updatedCity = city.copy(isFavorite = isFavorite)
            cityDao.updateCity(updatedCity)
        }
    }

    override fun getCityById(cityId: Int): Flow<City?> {
        return cityDao.getCityById(cityId).flowOn(Dispatchers.IO)
    }
}