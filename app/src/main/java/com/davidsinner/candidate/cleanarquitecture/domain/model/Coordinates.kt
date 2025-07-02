package com.davidsinner.candidate.cleanarquitecture.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable


@Serializable
@JsonClass(generateAdapter = true)
data class Coordinates(
    @Json(name = "lon") val longitude: Double,
    @Json(name = "lat") val latitude: Double
)