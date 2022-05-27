package com.example.travelplaner.accommodation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.db.AccommodationWithCity
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.destinations.AccommodationScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccommodationViewModel @Inject constructor(
    accommodationDao: AccommodationDao,
    savedStateHandle: SavedStateHandle,
    dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val _accommodation = MutableStateFlow<AccommodationWithCity?>(null)
    val accommodation = _accommodation.asStateFlow()

    init {
        val navArgs = AccommodationScreenDestination.argsFrom(savedStateHandle)

        viewModelScope.launch {
            val accommodationWithCity = withContext(dispatchers.io) {
                accommodationDao.readById(navArgs.accommodationId)
            }

            _accommodation.update { accommodationWithCity }
        }
    }
}