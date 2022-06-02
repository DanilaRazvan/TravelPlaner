package com.example.travelplaner.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.db.*
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.data.db.dao.FlightDao
import com.example.travelplaner.core.data.db.dao.TripDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private data class TripsResult(
    val trips: List<Trip>
)
private data class FlightsResult(
    val flights: List<FlightWithCity>
)
private data class AccommodationsResult(
    val accommodations: List<AccommodationWithCity>
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val tripDao: TripDao,
    private val accommodationDao: AccommodationDao,
    private val flightDao: FlightDao
) : ViewModel() {

    private val initialState = FavoritesViewState()
    private val _viewState = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    fun removeTripFromFavorites(tripId: Long) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                tripDao.deleteById(tripId)
            }
        }
    }
    fun removeFlightFromFavorites(flightId: Long) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                val currentFlight = _viewState.value.flights.find { it.flight.id == flightId }!!.flight
                flightDao.update(
                    currentFlight.copy(
                        isFavorite = !currentFlight.isFavorite
                    )
                )
            }
        }
    }
    fun removeAccommodationFromFavorites(accommodationId: Long) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                val currentAccommodation = _viewState.value.accommodations.find { it.accommodation.id == accommodationId }!!.accommodation
                accommodationDao.update(
                    currentAccommodation.copy(
                        isFavorite = !currentAccommodation.isFavorite
                    )
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                merge(
                    tripDao.readAllFlow()
                        .map { TripsResult(it) },
                    flightDao.readAllByFavoriteFlow(isFavorite = true)
                        .map { FlightsResult(it) },
                    accommodationDao.readAllByFavoriteFlow(isFavorite = true)
                        .map { AccommodationsResult(it) },
                ).scan(
                    initial = initialState
                ) { oldState, event ->
                    when (event) {
                        is TripsResult -> {
                            oldState.copy(
                                trips = event.trips
                            )
                        }
                        is FlightsResult -> {
                            oldState.copy(
                                flights = event.flights
                            )
                        }
                        is AccommodationsResult -> {
                            oldState.copy(
                                accommodations = event.accommodations
                            )
                        }

                        else -> oldState
                    }
                }.collect { newState ->
                    _viewState.update { newState }
                }
            }
        }
    }
}

data class FavoritesViewState(
    val trips: List<Trip> = emptyList(),
    val flights: List<FlightWithCity> = emptyList(),
    val accommodations: List<AccommodationWithCity> = emptyList()
)