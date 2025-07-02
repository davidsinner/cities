package com.davidsinner.candidate.cleanarquitecture.appdi


import android.content.Context
import androidx.room.Room
import com.davidsinner.candidate.cleanarquitecture.data.CityApiService
import com.davidsinner.candidate.cleanarquitecture.data.network.NetworkInterceptor
import com.davidsinner.candidate.cleanarquitecture.data.repository.CityRepositoryImpl
import com.davidsinner.candidate.cleanarquitecture.data.room.AppDatabase
import com.davidsinner.candidate.cleanarquitecture.data.room.CityDao
import com.davidsinner.candidate.cleanarquitecture.domain.repository.ICityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String =
        "https://gist.githubusercontent.com/hernan-uala/dce8843a8edbe0b0018b32e137bc2b3a/raw/0996accf70cb0ca0e16f9a99e0ee185fafca7af1/"

    @Provides
    @Singleton
    fun provideHeaderInterceptor(): NetworkInterceptor {
        return NetworkInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(networkInterceptor: NetworkInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(networkInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "city_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCityDao(database: AppDatabase): CityDao {
        return database.cityDao()
    }

    @Provides
    @Singleton
    fun provideCityApiService(retrofit: Retrofit): CityApiService {
        return retrofit.create(CityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCityRepository(
        apiService: CityApiService,
        cityDao: CityDao
    ): ICityRepository {
        return CityRepositoryImpl(apiService, cityDao)
    }
}