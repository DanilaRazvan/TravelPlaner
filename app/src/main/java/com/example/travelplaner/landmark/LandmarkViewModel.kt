package com.example.travelplaner.landmark

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.db.Landmark
import com.example.travelplaner.core.data.db.dao.LandmarkDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.destinations.LandmarkScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LandmarkViewModel @Inject constructor(
    landmarkDao: LandmarkDao,
    savedStateHandle: SavedStateHandle,
    dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val _viewState = MutableStateFlow<Landmark?>(null)
    val viewState = _viewState.asStateFlow()

    init {
        val navArgs = LandmarkScreenDestination.argsFrom(savedStateHandle)

        viewModelScope.launch {
            val landmark = withContext(dispatchers.io) {
                landmarkDao.readById(navArgs.landmarkId)
            }

            _viewState.update { landmark }
        }
    }
}