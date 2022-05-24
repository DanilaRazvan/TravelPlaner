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

// TODO: FILTERS

class GetHomeItemsUseCase @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val cityDao: CityDao,
    private val flightDao: FlightDao,
    private val accommodationDao: AccommodationDao,
    private val appDataRepository: AppDataRepository
) {
    suspend operator fun invoke(
        searchByDestinationFlow: SharedFlow<String>,
        searchByFromDateFlow: SharedFlow<Long?>,
        searchByToDateFlow: SharedFlow<Long?>,
    ): Flow<GetHomeItemsResult> = withContext(dispatchers.io) {

        val citiesFlow = combine(
            cityDao.readAllFlow(),
            searchByDestinationFlow
                .onSubscription { emit("") },
            appDataRepository.getEditMode(),
        ) { cities, searchText, isEditModeEnabled ->
            val filteredCities =
                if (isEditModeEnabled || (!isEditModeEnabled && searchText.isBlank())) {
                    cities
                } else {
                    cities.filter { model ->
                        model.name.lowercase().contains(searchText.lowercase()) ||
                                model.country.lowercase().contains(searchText.lowercase())
                    }
                }

            filteredCities.map { city ->
                TpListItemUiModel(
                    id = city.id,
                    imageUrl = city.photoUrl,
                    city = city.name,
                    country = city.country,
                    details = city.description
                )
            }
        }

        val flightsFlow = combine(
            flightDao.readAllFlow(),
            searchByDestinationFlow
                .onSubscription { emit("") },
            searchByFromDateFlow
                .onSubscription { emit(null) },
            searchByToDateFlow
                .onSubscription { emit(null) },
            appDataRepository.getEditMode(),
        ) { flights, searchText, from: Long?, to: Long?, isEditModeEnabled ->

            val filteredFlights = if (isEditModeEnabled) flights
            else {
                when {
                    searchText.isNotBlank() && from != null && to != null -> flights
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase()) &&
                                    model.flight.from <= from &&
                                    model.flight.to >= to
                        }

                    searchText.isNotBlank() && from != null -> flights
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase()) &&
                                    model.flight.from >= from
                        }

                    searchText.isNotBlank() && to != null -> flights
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase()) &&
                                    model.flight.to < to
                        }

                    from != null && to != null -> flights.filter { model ->
                        model.flight.from <= from &&
                                model.flight.to >= to
                    }

                    searchText.isNotBlank() -> flights
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase())
                        }

                    from != null -> flights
                        .filter { model ->
                            model.flight.from >= from
                        }

                    to != null -> flights
                        .filter { model ->
                            model.flight.to < to
                        }

                    else -> flights
                }
            }

            filteredFlights.map { entity ->
                TpListItemUiModel(
                    id = entity.flight.id,
                    imageUrl = entity.city.photoUrl,
                    city = entity.city.name,
                    country = entity.city.country,
                    details = "${entity.flight.ticketPrice} - ${entity.flight.duration}",
                    from = entity.flight.from,
                    to = entity.flight.to
                )
            }
        }

        val accommodationsFlow = combine(
            accommodationDao.readAllFlow(),
            searchByDestinationFlow
                .onSubscription { emit("") },
            searchByFromDateFlow
                .onSubscription { emit(null) },
            searchByToDateFlow
                .onSubscription { emit(null) },
            appDataRepository.getEditMode(),
        ) { accommodations, searchText, from: Long?, to: Long?, isEditModeEnabled ->

            val filteredAccommodations = if (isEditModeEnabled) accommodations
            else {
                when {
                    searchText.isNotBlank() && from != null && to != null -> accommodations
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase()) &&
                                    model.accommodation.from <= from &&
                                    model.accommodation.to >= to
                        }

                    searchText.isNotBlank() && from != null -> accommodations
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase()) &&
                                    model.accommodation.from >= from
                        }

                    searchText.isNotBlank() && to != null -> accommodations
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase()) &&
                                    model.accommodation.to < to
                        }

                    from != null && to != null -> accommodations.filter { model ->
                        model.accommodation.from <= from &&
                                model.accommodation.to >= to
                    }

                    searchText.isNotBlank() -> accommodations
                        .filter { model ->
                            model.city.name.contains(searchText.lowercase()) || model.city.country.lowercase()
                                .contains(searchText.lowercase())
                        }

                    from != null -> accommodations
                        .filter { model ->
                            model.accommodation.from >= from
                        }

                    to != null -> accommodations
                        .filter { model ->
                            model.accommodation.to < to
                        }

                    else -> accommodations
                }
            }

            filteredAccommodations.map { entity ->
                TpListItemUiModel(
                    id = entity.accommodation.id,
                    imageUrl = entity.accommodation.photoUrl,
                    city = entity.city.name,
                    country = entity.city.country,
                    details = entity.accommodation.description,
                    from = entity.accommodation.from,
                    to = entity.accommodation.to
                )
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