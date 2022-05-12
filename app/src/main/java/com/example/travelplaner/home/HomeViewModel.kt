package com.example.travelplaner.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.db.Accommodation
import com.example.travelplaner.core.data.db.City
import com.example.travelplaner.core.data.db.Flight
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.data.db.dao.CityDao
import com.example.travelplaner.core.data.db.dao.FlightDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.core.ui.model.TpListItemUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    getHomeItemsUseCase: GetHomeItemsUseCase,
    appDataRepository: AppDataRepository,
    cityDao: CityDao,
    flightDao: FlightDao,
    accommodationDao: AccommodationDao,
) : ViewModel() {

    private val initialState = HomeViewState()
    private val _viewState = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    private val pendingActions = MutableSharedFlow<HomeEvent>()

    val onSearchTextChanged: (String) -> Unit = {
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.SearchTextChanged(it))
        }
    }
    val onSearchByDestination: () -> Unit = {
        viewModelScope.launch {

            _viewState.update { it.copy(isLoading = true) }

            pendingActions.emit(
                HomeEvent.SearchByDestination(
                    destination = _viewState.value.searchText
                )
            )
        }
    }
    val onScreenChanged: (HomeScreen) -> Unit = { selectedScreen ->
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.ScreenChanged(selectedScreen))
        }
    }
    val onToggleEditMode: () -> Unit = {
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.ToggleEditMode)
        }
    }

    fun onAddCity(
        name: String,
        country: String,
        imageUrl: String,
        description: String
    ) {
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.AddCity(name, country, imageUrl, description))
        }
    }
    fun onAddFlight(
        toCityId: Long,
        duration: String,
        ticketPrice: String
    ) {
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.AddFlight(toCityId, ticketPrice, duration))
        }
    }
    fun onAddAccommodation(
        cityId: Long,
        name: String,
        imageUrl: String,
        description: String
    ) {
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.AddAccommodation(cityId, name, description, imageUrl))
        }
    }

    fun onRemoveElement(
        elementId: Long,
        selectedScreen: HomeScreen,
    ) {
        viewModelScope.launch {
            pendingActions.emit(HomeEvent.RemoveElement(elementId, selectedScreen))
        }
    }

    init {
        viewModelScope.launch(dispatchers.computation) {
            merge<Any>(
                pendingActions.filterIsInstance<HomeEvent.ScreenChanged>(),
                pendingActions.filterIsInstance<HomeEvent.SearchTextChanged>()
                    .transform {
                        emit(it)
                        if (it.text.isBlank()) {
                            pendingActions.emit(HomeEvent.SearchByDestination(""))
                        }
                    },
                getHomeItemsUseCase(
                    pendingActions.filterIsInstance<HomeEvent.SearchByDestination>()
                        .map { it.destination }
                        .shareIn(viewModelScope, SharingStarted.WhileSubscribed()),
                ),
                pendingActions.filterIsInstance<HomeEvent.ToggleEditMode>()
                    .transform {
                        appDataRepository.toggleEditModePreference()
                    },
                pendingActions.filterIsInstance<HomeEvent.AddCity>()
                    .transform {
                        withContext(dispatchers.io) {
                            cityDao.insert(
                                City(
                                    name = it.name,
                                    country = it.country,
                                    photoUrl = it.imageUrl,
                                    description = it.description
                                )
                            )
                        }
                    },
                pendingActions.filterIsInstance<HomeEvent.AddFlight>()
                    .transform {
                        withContext(dispatchers.io) {
                            flightDao.insert(
                                Flight(
                                    ticketPrice = it.ticketPrice,
                                    duration = it.duration,
                                    toCityId = it.toCityId
                                )
                            )
                        }
                    },
                pendingActions.filterIsInstance<HomeEvent.AddAccommodation>()
                    .transform {
                        withContext(dispatchers.io) {
                            accommodationDao.insert(
                                Accommodation(
                                    name = it.name,
                                    description = it.description,
                                    photoUrl = it.imageUrl,
                                    cityId = it.cityId
                                )
                            )
                        }
                    },
                pendingActions.filterIsInstance<HomeEvent.RemoveElement>()
                    .transform {
                        withContext(dispatchers.io) {
                            when (it.selectedScreen) {
                                HomeScreen.Visit -> {
                                    flightDao.deleteByCityId(it.id)
                                    accommodationDao.deleteByCityId(it.id)
                                    cityDao.deleteById(it.id)
                                }
                                HomeScreen.Fly -> {
                                    flightDao.deleteById(it.id)
                                }
                                HomeScreen.Sleep -> {
                                    accommodationDao.deleteById(it.id)
                                }
                            }
                        }
                    }
            ).scan(initialState) { oldState, event ->
                when (event) {
                    is HomeEvent.SearchTextChanged -> {
                        oldState.copy(
                            searchText = event.text,
                            isLoading = false
                        )
                    }
                    is HomeEvent.ScreenChanged -> {
                        oldState.copy(
                            selectedScreen = event.selectedScreen
                        )
                    }
                    is GetHomeItemsResult.Success -> {
                        oldState.copy(
                            isLoading = false,
                            cities = event.cities,
                            flights = event.flights,
                            accommodations = event.accommodations,
                            isEditModeEnabled = event.isEditModeEnabled
                        )
                    }
                    else -> oldState
                }
            }.collect { newState ->
                _viewState.update {
                    newState
                }
            }
        }
    }
}

data class HomeViewState(
    val searchText: String = "",
    val selectedScreen: HomeScreen = HomeScreen.Visit,
    val cities: List<TpListItemUiModel> = emptyList(),
    val flights: List<TpListItemUiModel> = emptyList(),
    val accommodations: List<TpListItemUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val isEditModeEnabled: Boolean = false
)

sealed class HomeEvent {
    data class SearchTextChanged(
        val text: String
    ) : HomeEvent()

    data class SearchByDestination(
        val destination: String
    ) : HomeEvent()

    data class ScreenChanged(
        val selectedScreen: HomeScreen
    ) : HomeEvent()

    object ToggleEditMode : HomeEvent()

    data class AddCity(
        val name: String,
        val country: String,
        val imageUrl: String,
        val description: String
    ) : HomeEvent()

    data class AddFlight(
        val toCityId: Long,
        val ticketPrice: String,
        val duration: String
    ) : HomeEvent()

    data class AddAccommodation(
        val cityId: Long,
        val name: String,
        val description: String,
        val imageUrl: String
    ) : HomeEvent()

    data class RemoveElement(
        val id: Long,
        val selectedScreen: HomeScreen,
    ) : HomeEvent()
}

enum class HomeScreen {
    Visit, Fly, Sleep
}

