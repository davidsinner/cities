package com.davidsinner.candidate.cleanarquitecture.presentation.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {

    val CityType = object : NavType<City>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): City? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): City {
            return Json.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: City): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: City) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}