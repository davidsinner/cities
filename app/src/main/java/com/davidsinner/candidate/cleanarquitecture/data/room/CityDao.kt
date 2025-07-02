package com.davidsinner.candidate.cleanarquitecture.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<City>)

    @Query("SELECT * FROM cities")
    fun getCitiesPaged(): PagingSource<Int, City>

    @Query("SELECT * FROM cities WHERE name LIKE '%' || :query || '%' OR country LIKE '%' || :query || '%'")
    fun searchCitiesPaged(query: String): PagingSource<Int, City>

    @Query("SELECT * FROM cities WHERE isFavorite = 1")
    fun getFavoriteCitiesPaged(): PagingSource<Int, City>

    @Update
    suspend fun updateCity(city: City)

    @Query("SELECT * FROM cities WHERE id = :cityId")
    fun getCityById(cityId: Int): Flow<City?>

    @Query("DELETE FROM cities")
    suspend fun clearAllCities()
}