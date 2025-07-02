package com.davidsinner.candidate.cleanarquitecture.domain.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@Serializable
@JsonClass(generateAdapter = true)
@Entity(tableName = "cities")
data class City(
    @Json(name = "_id")
    @PrimaryKey
    val id: Int,

    @Json(name = "country")
    val country: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "coord")
    val coord: Coordinates,

    val isFavorite: Boolean = false
)