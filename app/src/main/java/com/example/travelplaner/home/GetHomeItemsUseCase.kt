package com.example.travelplaner.home

import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.data.db.dao.CityDao
import com.example.travelplaner.core.data.db.dao.FlightDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.core.ui.model.TpListItemUiModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetHomeItemsUseCase @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val cityDao: CityDao,
    private val flightDao: FlightDao,
    private val accommodationDao: AccommodationDao,
    private val appDataRepository: AppDataRepository
) {
    suspend operator fun invoke(
        searchByDestinationFlow: SharedFlow<String>
    ): Flow<GetHomeItemsResult> = withContext(dispatchers.io) {

        val citiesFlow = combine(
            cityDao.readAllFlow()
                .map { cities ->
                    cities.map { city ->
                        TpListItemUiModel(
                            id = city.id,
                            imageUrl = city.photoUrl,
                            city = city.name,
                            country = city.country,
                            details = city.description
                        )
                    }
                },
            searchByDestinationFlow
                .onSubscription { emit("") },
            appDataRepository.getEditMode(),
        ) { cities, searchText, isEditModeEnabled ->
            if (isEditModeEnabled || (!isEditModeEnabled && searchText.isBlank())) {
                cities
            } else {
                cities.filter { model ->
                    model.city.lowercase().contains(searchText.lowercase()) ||
                            model.country.lowercase().contains(searchText.lowercase())
                }
            }
        }
        val flightsFlow = combine(
            flightDao.readAllFlow()
                .map { flights ->
                    flights.map { entity ->
                        TpListItemUiModel(
                            id = entity.flight.id,
                            imageUrl = entity.city.photoUrl,
                            city = entity.city.name,
                            country = entity.city.country,
                            details = "${entity.flight.ticketPrice} - ${entity.flight.duration}"
                        )
                    }
                },
            searchByDestinationFlow
                .onSubscription { emit("") },
            appDataRepository.getEditMode(),
        ) { flights, searchText, isEditModeEnabled ->
            if (isEditModeEnabled || (!isEditModeEnabled && searchText.isBlank())) {
                flights
            } else {
                flights.filter { model ->
                    model.city.lowercase().contains(searchText.lowercase()) ||
                            model.country.lowercase().contains(searchText.lowercase())
                }
            }
        }

        val accommodationsFlow = combine(
            accommodationDao.readAllFlow()
                .map { accommodations ->
                    accommodations.map { entity ->
                        TpListItemUiModel(
                            id = entity.accommodation.id,
                            imageUrl = entity.accommodation.photoUrl,
                            city = entity.city.name,
                            country = entity.city.country,
                            details = entity.accommodation.description
                        )
                    }
                },
            searchByDestinationFlow
                .onSubscription { emit("") },
            appDataRepository.getEditMode(),
        ) { accommodations, searchText, isEditModeEnabled ->
            if (isEditModeEnabled || (!isEditModeEnabled && searchText.isBlank())) {
                accommodations
            } else {
                accommodations.filter { model ->
                    model.city.lowercase().contains(searchText.lowercase()) ||
                            model.country.lowercase().contains(searchText.lowercase())
                }
            }
        }

        return@withContext combine(
            citiesFlow,
            flightsFlow,
            accommodationsFlow,
            appDataRepository.getEditMode()
        ) { cities, flights, accommodations, isEditModeEnabled ->

            GetHomeItemsResult.Success(cities, flights, accommodations, isEditModeEnabled)
        }
    }
}

sealed class GetHomeItemsResult {
    data class Success(
        val cities: List<TpListItemUiModel>,
        val flights: List<TpListItemUiModel>,
        val accommodations: List<TpListItemUiModel>,
        val isEditModeEnabled: Boolean
    ) : GetHomeItemsResult()
}