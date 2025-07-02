package com.davidsinner.candidate.cleanarquitecture.data.util


import androidx.room.TypeConverter
import com.davidsinner.candidate.cleanarquitecture.domain.model.Coordinates
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val coordinatesAdapter = moshi.adapter(Coordinates::class.java)

    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates?): String? {
        return coordinates?.let { coordinatesAdapter.toJson(it) }
    }

    @TypeConverter
    fun toCoordinates(coordinatesString: String?): Coordinates? {
        return coordinatesString?.let { coordinatesAdapter.fromJson(it) }
    }
}