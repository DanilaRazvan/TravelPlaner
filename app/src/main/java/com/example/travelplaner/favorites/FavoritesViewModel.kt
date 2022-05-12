package com.example.travelplaner.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.db.Trip
import com.example.travelplaner.core.data.db.dao.TripDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val tripDao: TripDao
) : ViewModel() {

    private val initialState = FavoritesViewState()
    private val _viewState = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    fun removeFromFavorites(tripId: Long) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                tripDao.deleteById(tripId)
            }
        }
    }

    init {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                tripDao.readAllFlow()
                    .collect { trips ->
                        _viewState.update {
                            it.copy(
                                trips = trips
                            )
                        }
                    }
            }
        }
    }
}

data class FavoritesViewState(
    val trips: List<Trip> = emptyList()
)