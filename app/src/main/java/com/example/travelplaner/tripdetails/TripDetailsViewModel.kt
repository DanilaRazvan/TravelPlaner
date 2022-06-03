package com.example.travelplaner.tripdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.db.*
import com.example.travelplaner.core.data.db.dao.CityDao
import com.example.travelplaner.core.data.db.dao.LandmarkDao
import com.example.travelplaner.core.data.db.dao.TripDao
import com.example.travelplaner.core.data.db.dao.TripsLandmarksDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.core.ui.model.LandmarkUiModel
import com.example.travelplaner.destinations.TripDetailsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TripDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    appDataRepository: AppDataRepository,
    cityDao: CityDao,
    landmarkDao: LandmarkDao,
    dispatchers: AppCoroutineDispatchers,
    tripDao: TripDao,
    tripsLandmarksDao: TripsLandmarksDao
) : ViewModel() {
    private val initialState = TripDetailsViewState()
    private val _viewState = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    private val pendingActions = MutableSharedFlow<TripDetailsEvent>()

    fun onAddLandmark(
        landmarkName: String,
        ticketPrice: String,
        imageUrl: String,
        from: String,
        until: String,
        description: String
    ) {
        viewModelScope.launch {
            val price = ticketPrice.replace(",", ".").filter { it.isDigit() || it == '.' }.trim()

            pendingActions.emit(
                TripDetailsEvent.AddLandmark(
                    landmarkName = landmarkName,
                    ticketPrice = if (price.isBlank()) 0f else price.toFloat(),
                    imageUrl = imageUrl,
                    from = from,
                    until = until,
                    description = description
                )
            )
        }
    }

    fun removeLandmark(
        id: Long
    ) {
        viewModelScope.launch {
            pendingActions.emit(TripDetailsEvent.RemoveLandmark(id))
        }
    }

    fun excludeFromTrip(
        landmark: LandmarkUiModel
    ) {
        viewModelScope.launch {
            pendingActions.emit(TripDetailsEvent.ExcludeFromTrip(landmark))
        }
    }

    fun onSaveTrip() {
        viewModelScope.launch {
            pendingActions.emit(TripDetailsEvent.SaveTrip)
        }
    }

    init {
        val navArgs = TripDetailsScreenDestination.argsFrom(savedStateHandle)

        viewModelScope.launch {
            merge(
                appDataRepository.getEditMode(),
                if (navArgs.cityId != -1L) {
                    cityDao.readWithLandmarksById(navArgs.cityId)
                } else {
                    flowOf(tripDao.readWithLandmarksById(navArgs.tripId))
                },
                pendingActions.filterIsInstance<TripDetailsEvent.AddLandmark>()
                    .transform {
                        withContext(dispatchers.io) {
                            landmarkDao.insert(
                                Landmark(
                                    name = it.landmarkName,
                                    ticketPrice = it.ticketPrice,
                                    photoUrl = it.imageUrl,
                                    description = it.description,
                                    cityId = navArgs.cityId,
                                    from = it.from,
                                    until = it.until,
                                    additionalPhotos = emptyList()
                                )
                            )
                        }
                    },
                pendingActions.filterIsInstance<TripDetailsEvent.RemoveLandmark>()
                    .transform {
                        withContext(dispatchers.io) {
                            landmarkDao.deleteById(it.id)
                        }
                    },
                pendingActions.filterIsInstance<TripDetailsEvent.ExcludeFromTrip>(),
                pendingActions.filterIsInstance<TripDetailsEvent.SaveTrip>()
                    .transform {
                        withContext(dispatchers.io) {
                            val tripId = tripDao.insert(
                                Trip(
                                    name = viewState.value.destination,
                                    cityId = navArgs.cityId,
                                    totalPrice = viewState.value.totalPrice
                                )
                            )

                            viewState.value.landmarks.forEach { landmark ->
                                tripsLandmarksDao.insert(
                                    TripLandmarkCrossRef(
                                        tripId = tripId,
                                        landmarkId = landmark.id
                                    )
                                )
                            }
                        }

                        emit(it)
                    }
            ).scan(
                initialState.copy(
                    canRemoveLandmarks = navArgs.cityId != -1L,
                    showSaveFavoriteIcon = navArgs.cityId != -1L
                )
            ) { oldState, event ->
                when (event) {
                    TripDetailsEvent.SaveTrip -> oldState.copy(
                        tripSaved = true
                    )
                    is TripDetailsEvent.ExcludeFromTrip -> oldState.copy(
                        totalPrice = oldState.totalPrice - event.landmark.price,
                        landmarks = oldState.landmarks.toMutableList()
                            .apply { remove(event.landmark) }
                    )
                    is CityWithLandmarks -> oldState.copy(
                        destination = event.city.name + ", " + event.city.country,
                        totalPrice = event.landmarks.sumOf { it.ticketPrice.toDouble() }.toFloat(),
                        landmarks = event.toTpList()
                    )
                    is Boolean -> oldState.copy(isEditModeEnabled = event)
                    is TripWithLandmarks -> {
                        oldState.copy(
                            destination = event.trip.name,
                            totalPrice = event.trip.totalPrice,
                            landmarks = event.landmarks.map { it.toTpListItem() }
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

data class TripDetailsViewState(
    val destination: String = "",
    val totalPrice: Float = 0f,
    val landmarks: List<LandmarkUiModel> = emptyList(),

    val showSaveFavoriteIcon: Boolean = true,
    val canRemoveLandmarks: Boolean = true,

    val isEditModeEnabled: Boolean = false,
    val tripSaved: Boolean = false,
)

sealed class TripDetailsEvent {
    data class AddLandmark(
        val landmarkName: String,
        val ticketPrice: Float,
        val imageUrl: String,
        val from: String,
        val until: String,
        val description: String
    ) : TripDetailsEvent()

    data class RemoveLandmark(
        val id: Long,
    ) : TripDetailsEvent()

    data class ExcludeFromTrip(
        val landmark: LandmarkUiModel,
    ) : TripDetailsEvent()

    object SaveTrip : TripDetailsEvent()
}

private fun CityWithLandmarks.toTpList(): List<LandmarkUiModel> {
    return this.landmarks.map {
        LandmarkUiModel(
            id = it.id,
            imageUrl = it.photoUrl,
            name = it.name,
            details = it.description,
            price = it.ticketPrice
        )
    }
}

private fun Landmark.toTpListItem(): LandmarkUiModel {
    return LandmarkUiModel(
        id = this.id,
        imageUrl = this.photoUrl,
        name = this.name,
        details = this.description,
        price = this.ticketPrice
    )
}