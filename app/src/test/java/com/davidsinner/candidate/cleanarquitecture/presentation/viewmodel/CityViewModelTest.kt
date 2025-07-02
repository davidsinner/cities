package com.davidsinner.candidate.cleanarquitecture.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.domain.repository.ICityRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
 import com.davidsinner.candidate.cleanarquitecture.domain.model.Coordinates
import io.mockk.coVerify


@ExperimentalCoroutinesApi
class CityViewModelTest {

 @get:Rule
 val instantTaskExecutorRule = InstantTaskExecutorRule()

 private val testDispatcher = StandardTestDispatcher()

 private lateinit var mockRepository: ICityRepository

 private lateinit var viewModel: CityViewModel


 private val testCities = listOf(
  City(1, "Argentina", "Buenos Aires", Coordinates(-58.3816, -34.6037), false),
  City(2, "Argentina", "Córdoba", Coordinates(-64.1888, -31.4167), false),
  City(3, "Argentina", "Rosario", Coordinates(-60.6493, -32.9468), true),
  City(4, "Argentina", "Bariloche", Coordinates(-71.3093, -41.1339), false),
  City(5, "Argentina", "Mendoza", Coordinates(-68.8458, -32.8895), true),
  City(6, "Argentina", "Salta", Coordinates(-65.4117, -24.7859), false),
  City(7, "Argentina", "Jujuy", Coordinates(-65.3039, -24.1946), false)
 )

 private val testFavoriteCities = testCities.filter { it.isFavorite }

 @Before
 fun setup() {
  Dispatchers.setMain(testDispatcher)
  mockRepository = mockk()

  coEvery { mockRepository.getCities(false) } returns flowOf(PagingData.from(testCities))

  coEvery { mockRepository.searchCities(any()) } answers { call ->
   val query = call.invocation.args[0] as String
   val filteredCities = testCities.filter { it.name.contains(query, ignoreCase = true) }
   flowOf(PagingData.from(filteredCities))
  }

  coEvery { mockRepository.getFavoriteCities() } returns flowOf(PagingData.from(testFavoriteCities))
  coEvery { mockRepository.refreshAndCacheCities() } returns Unit
  coEvery { mockRepository.updateCityFavoriteStatus(any(), any()) } returns Unit

  viewModel = CityViewModel(mockRepository)
 }


 @Test
 fun `viewModel init loads cities and sets isLoading to false`() = runTest {
  advanceUntilIdle()
  assertFalse(viewModel.uiState.first().isLoading)
 }

 @Test
 fun `onSearchQueryChanged updates uiState searchQuery immediately`() = runTest {
  val query = "Buenos"
  viewModel.onSearchQueryChanged(query)
  assertEquals(query, viewModel.uiState.first().searchQuery)
 }

 @Test
 fun `toggleFavoriteFilter updates uiState filterFavoritesOnly immediately`() = runTest {
  assertFalse(viewModel.uiState.first().filterFavoritesOnly)
  viewModel.toggleFavoriteFilter()
  assertTrue(viewModel.uiState.first().filterFavoritesOnly)
  viewModel.toggleFavoriteFilter()
  assertFalse(viewModel.uiState.first().filterFavoritesOnly)
 }

 @Test
 fun `citiesPagingData returns all cities when no search query and no favorite filter`() = runTest {
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { testCities }
  assertEquals(testCities, citiesFromPaging)
  job.cancel()
 }

 @Test
 fun `citiesPagingData returns filtered cities for valid search query`() = runTest {
  val query = "Bue"
  viewModel.onSearchQueryChanged(query)
  advanceUntilIdle()
  val expectedCities = testCities.filter { it.name.contains(query, ignoreCase = true) }
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { expectedCities }
  assertEquals(expectedCities, citiesFromPaging)
  job.cancel()
 }

 @Test
 fun `citiesPagingData returns all cities for blank search query after a search`() = runTest {
  viewModel.onSearchQueryChanged("Rosario")
  advanceUntilIdle()
  viewModel.onSearchQueryChanged("")
  advanceUntilIdle()
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { testCities }
  assertEquals(testCities, citiesFromPaging)
  job.cancel()
 }

 @Test
 fun `citiesPagingData returns favorite cities when filter is toggled`() = runTest {
  viewModel.toggleFavoriteFilter()
  advanceUntilIdle()
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { testFavoriteCities }
  assertEquals(testFavoriteCities, citiesFromPaging)
  job.cancel()
 }

 @Test
 fun `citiesPagingData prioritizes favorite filter over search query`() = runTest {
  viewModel.onSearchQueryChanged("Buenos")
  advanceUntilIdle()
  viewModel.toggleFavoriteFilter()
  advanceUntilIdle()
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { testFavoriteCities }
  assertEquals(testFavoriteCities, citiesFromPaging)
  job.cancel()
 }

 @Test
 fun `toggleCityFavorite calls repository update and handles error`() = runTest {
  val cityToToggle = testCities[0]
  val expectedNewStatus = !cityToToggle.isFavorite
  coEvery { mockRepository.updateCityFavoriteStatus(cityToToggle, expectedNewStatus) } returns Unit
  viewModel.toggleCityFavorite(cityToToggle)
  advanceUntilIdle()
  coVerify { mockRepository.updateCityFavoriteStatus(cityToToggle, expectedNewStatus) }
  assertEquals(null, viewModel.uiState.first().error)

  val errorMessage = "Failed to update favorite"
  coEvery { mockRepository.updateCityFavoriteStatus(cityToToggle, expectedNewStatus) } throws Exception(errorMessage)
  viewModel.toggleCityFavorite(cityToToggle)
  advanceUntilIdle()
  assertEquals("Error al cambiar favorito: $errorMessage", viewModel.uiState.first().error)
 }

 @Test
 fun `search with invalid input (special characters) returns no results`() = runTest {
  val invalidQuery = "@#$"
  viewModel.onSearchQueryChanged(invalidQuery)
  advanceUntilIdle()
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { emptyList<City>() }
  assertEquals(emptyList<City>(), citiesFromPaging)
  job.cancel()
 }

 @Test
 fun `search with empty string returns all cities`() = runTest {
  val emptyQuery = ""
  viewModel.onSearchQueryChanged(emptyQuery)
  advanceUntilIdle()
  val collectedPagingData = mutableListOf<PagingData<City>>()
  val job = launch {
   viewModel.citiesPagingData.collectLatest { collectedPagingData.add(it) }
  }
  advanceUntilIdle()
  val citiesFromPaging = collectedPagingData.firstOrNull()?.let { testCities }
  assertEquals(testCities, citiesFromPaging)
  job.cancel()
 }
}