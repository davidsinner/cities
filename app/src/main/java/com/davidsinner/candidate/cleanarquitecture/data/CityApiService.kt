package com.davidsinner.candidate.cleanarquitecture.data

import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import retrofit2.http.GET

interface CityApiService {
    @GET("cities.json")
    suspend fun getCities(): List<City>
}