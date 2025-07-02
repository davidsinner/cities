package com.davidsinner.candidate.cleanarquitecture.presentation.navigation

import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import kotlinx.serialization.Serializable

@Serializable
object CityListDestination

@Serializable
data class CityDetailDestination(
    val city: City
)

@Serializable
data class MapDestination(
    val city: City
)