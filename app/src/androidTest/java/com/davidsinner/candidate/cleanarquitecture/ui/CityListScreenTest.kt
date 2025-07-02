package com.davidsinner.candidate.cleanarquitecture.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.davidsinner.candidate.cleanarquitecture.domain.model.City
import com.davidsinner.candidate.cleanarquitecture.domain.model.Coordinates
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityDetailScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityListItem
import com.davidsinner.candidate.cleanarquitecture.presentation.screen.CityListScreen
import com.davidsinner.candidate.cleanarquitecture.presentation.state.CityListState
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CityScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testCities = listOf(
        City(1, "Argentina", "Buenos Aires", Coordinates(-58.3816, -34.6037), false),
        City(2, "Argentina", "Córdoba", Coordinates(-64.1888, -31.4167), false),
        City(3, "Argentina", "Rosario", Coordinates(-60.6493, -32.9468), true),
        City(4, "Argentina", "Bariloche", Coordinates(-71.3093, -41.1339), false),
        City(5, "Argentina", "Mendoza", Coordinates(-68.8458, -32.8895), true)
    )


    private val mockOnSearchQueryChanged: (String) -> Unit = mockk(relaxed = true)
    private val mockOnToggleFavoriteFilter: () -> Unit = mockk(relaxed = true)
    private val mockOnToggleCityFavorite: (City) -> Unit = mockk(relaxed = true)
    private val mockOnNavigateToMap: (City) -> Unit = mockk(relaxed = true)
    private val mockOnNavigateToInfo: (City) -> Unit = mockk(relaxed = true)
    private val mockOnBack: () -> Unit = mockk(relaxed = true)

    @Test
    fun cityListScreen_displaysInitialElements() {
        val citiesFlow =
            MutableStateFlow(PagingData.from(emptyList<City>()))

        composeTestRule.setContent {
            MaterialTheme {
                CityListScreen(
                    uiState = CityListState(),
                    citiesPagingDataFlow = citiesFlow,
                    onSearchQueryChanged = mockOnSearchQueryChanged,
                    onToggleFavoriteFilter = mockOnToggleFavoriteFilter,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Buscar ciudad o país").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mostrar solo favoritos").assertIsDisplayed()

        citiesFlow.value = PagingData.from(testCities)

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onNodeWithText("Buenos Aires").isDisplayed() &&
                    composeTestRule.onNodeWithText("Córdoba").isDisplayed() &&
                    composeTestRule.onNodeWithText("Rosario").isDisplayed()
        }

        composeTestRule.onNodeWithText("Buenos Aires").assertIsDisplayed()
        composeTestRule.onNodeWithText("Córdoba").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rosario").assertIsDisplayed()
    }


    @Test
    fun cityListScreen_searchQueryUpdatesAndTriggersCallback() {
        composeTestRule.setContent {
            MaterialTheme {
                CityListScreen(
                    uiState = CityListState(),
                    citiesPagingDataFlow = flowOf(PagingData.from(testCities)),
                    onSearchQueryChanged = mockOnSearchQueryChanged,
                    onToggleFavoriteFilter = mockOnToggleFavoriteFilter,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }

        val searchText = "Mendoza"
        composeTestRule.onNodeWithText("Buscar ciudad o país").performTextInput(searchText)

        verify { mockOnSearchQueryChanged(searchText) }
    }

    @Test
    fun cityListScreen_toggleFavoriteFilterChangesStateAndTriggersCallback() {
        val initialUiState = CityListState(filterFavoritesOnly = false)
        val currentUiState = MutableStateFlow(initialUiState)

        composeTestRule.setContent {
            MaterialTheme {
                CityListScreen(
                    uiState = currentUiState.collectAsState().value, // Usa collectAsState()
                    citiesPagingDataFlow = flowOf(PagingData.from(testCities)),
                    onSearchQueryChanged = mockOnSearchQueryChanged,
                    onToggleFavoriteFilter = mockOnToggleFavoriteFilter,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }

        composeTestRule.onNodeWithText("Mostrar solo favoritos")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("favoriteFilterCheckbox")
            .assertIsOff()
        composeTestRule.onNodeWithTag("favoriteFilterCheckbox").performClick()

        verify { mockOnToggleFavoriteFilter() }

        currentUiState.value = currentUiState.value.copy(filterFavoritesOnly = true)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("favoriteFilterCheckbox").assertIsOn()
    }

    @Test
    fun cityListScreen_displaysLoadingState() {
        composeTestRule.setContent {
            MaterialTheme {
                CityListScreen(
                    uiState = CityListState(),
                    citiesPagingDataFlow = flowOf(PagingData.empty()),
                    onSearchQueryChanged = mockOnSearchQueryChanged,
                    onToggleFavoriteFilter = mockOnToggleFavoriteFilter,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No se encontraron ciudades.").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Error al cargar ciudades").assertIsNotDisplayed()
    }

    @Test
    fun cityListScreen_displaysErrorState() {
        composeTestRule.setContent {
            MaterialTheme {
                CityListScreen(
                    uiState = CityListState(error = "Test error message"),
                    citiesPagingDataFlow = flowOf(PagingData.from(testCities)),
                    onSearchQueryChanged = mockOnSearchQueryChanged,
                    onToggleFavoriteFilter = mockOnToggleFavoriteFilter,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }

        composeTestRule.onNodeWithText("Error de la aplicación: Test error message")
            .assertIsDisplayed()
    }

    @Test
    fun cityListItem_displaysCityInfoAndTriggersCallbacks() {
        val city = testCities[0] // Buenos Aires

        composeTestRule.setContent {
            MaterialTheme {
                CityListItem(
                    city = city,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }

        composeTestRule.onNodeWithText(city.name).assertIsDisplayed()
        composeTestRule.onNodeWithText(city.country).assertIsDisplayed()

        composeTestRule.onNodeWithText(city.name).performClick()
        verify { mockOnNavigateToInfo(city) }

        val favoriteButtonContentDesc =
            if (city.isFavorite) "Remove from favorites" else "Add to favorites"
        composeTestRule.onNodeWithContentDescription(favoriteButtonContentDesc).performClick()
        verify { mockOnToggleCityFavorite(city) }

        composeTestRule.onNodeWithContentDescription("Show on map").performClick()
        verify { mockOnNavigateToMap(city) }

        composeTestRule.onNodeWithContentDescription("More info").performClick()
        verify(atLeast = 2) { mockOnNavigateToInfo(city) }
    }

    @Test
    fun cityListItem_displaysNonFavoriteIconForNonFavoriteCity() {
        val nonFavoriteCity = testCities[0].copy(isFavorite = false) // Buenos Aires
        composeTestRule.setContent {
            MaterialTheme {
                CityListItem(
                    city = nonFavoriteCity,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsNotDisplayed()
    }

    @Test
    fun cityListItem_displaysFavoriteIconForFavoriteCity() {
        val favoriteCity = testCities[2].copy(isFavorite = true) // Rosario
        composeTestRule.setContent {
            MaterialTheme {
                CityListItem(
                    city = favoriteCity,
                    onToggleCityFavorite = mockOnToggleCityFavorite,
                    onNavigateToMap = mockOnNavigateToMap,
                    onNavigateToInfo = mockOnNavigateToInfo
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Remove from favorites").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add to favorites").assertIsNotDisplayed()
    }


    @Test
    fun cityDetailScreen_displaysCityDetailsAndTriggersBackCallback() {
        val city =
            testCities[0].copy(isFavorite = true)

        composeTestRule.setContent {
            MaterialTheme {
                CityDetailScreen(
                    city = city,
                    onBack = mockOnBack
                )
            }
        }

        composeTestRule.onNodeWithTag("cityDetailTitle").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Volver").assertIsDisplayed()

        composeTestRule.onNodeWithText(
            "Coordenadas: Lat ${
                String.format(
                    "%.4f",
                    city.coord.latitude
                )
            }, Lon ${String.format("%.4f", city.coord.longitude)}"
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("ID de la ciudad: ${city.id}").assertIsDisplayed()
        composeTestRule.onNodeWithText("Es favorita: Sí").assertIsDisplayed()
        composeTestRule.onNodeWithText("Información adicional (Lorem Ipsum):").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Volver").performClick()
        verify { mockOnBack() }
    }
}