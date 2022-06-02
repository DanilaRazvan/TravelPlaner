package com.example.travelplaner.flight

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.db.FlightWithCity
import com.example.travelplaner.core.data.db.dao.FlightDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.destinations.FlightScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FlightViewModel @Inject constructor(
    private val flightDao: FlightDao,
    savedStateHandle: SavedStateHandle,
    private val dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val _flight = MutableStateFlow<FlightWithCity?>(null)
    val flight = _flight.asStateFlow()

    init {
        val navArgs = FlightScreenDestination.argsFrom(savedStateHandle)

        viewModelScope.launch {
            withContext(dispatchers.io) {
                flightDao.readByIdFlow(navArgs.flightId)
                    .collect { newFlight ->
                        _flight.update {
                            newFlight
                        }
                    }
            }
        }
    }

    fun onToggleIsFavorite() {
        viewModelScope.launch(dispatchers.io) {
            val currentAccommodation = _flight.value!!.flight
            flightDao.update(
                currentAccommodation.copy(
                    isFavorite = !currentAccommodation.isFavorite
                )
            )
        }
    }
}