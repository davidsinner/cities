package com.davidsinner.candidate.cleanarquitecture.data.room


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidsinner.candidate.cleanarquitecture.data.util.Converters
import com.davidsinner.candidate.cleanarquitecture.domain.model.City

@Database(entities = [City::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
}