package com.example.travelplaner.landmark

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.db.Landmark
import com.example.travelplaner.core.data.db.dao.LandmarkDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.destinations.LandmarkScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LandmarkViewModel @Inject constructor(
    appDataRepository: AppDataRepository,
    landmarkDao: LandmarkDao,
    savedStateHandle: SavedStateHandle,
    dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val _viewState = MutableStateFlow<Landmark?>(null)
    val viewState = _viewState.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    val editMode = _editMode.asStateFlow()

    private val imageUrlAction = MutableSharedFlow<String>()
    private val deleteImageUrlAction = MutableSharedFlow<String>()

    init {
        val navArgs = LandmarkScreenDestination.argsFrom(savedStateHandle)

        viewModelScope.launch {
            withContext(dispatchers.io) {
                landmarkDao.readByIdFlow(navArgs.landmarkId)
                    .collect { landmark ->
                        _viewState.update { landmark }
                    }
            }
        }

        viewModelScope.launch {
            withContext(dispatchers.io) {

                appDataRepository.getEditMode()
                    .collect { editMode ->
                        _editMode.update {
                            editMode
                        }
                    }
            }
        }

        viewModelScope.launch(dispatchers.io) {
            imageUrlAction
                .collect {
                    val landmark = landmarkDao.readById(navArgs.landmarkId)
                    landmarkDao.update(
                        landmark.copy(
                            additionalPhotos = landmark.additionalPhotos + it
                        )
                    )
                }
        }

        viewModelScope.launch(dispatchers.io) {
            deleteImageUrlAction
                .collect {
                    val landmark = landmarkDao.readById(navArgs.landmarkId)
                    landmarkDao.update(
                        landmark.copy(
                            additionalPhotos = landmark.additionalPhotos - it
                        )
                    )
                }
        }
    }

    fun addPhoto(imageUrl: String) {
        viewModelScope.launch {
            imageUrlAction.emit(imageUrl)
        }
    }

    fun onDeleteImage(imageUrl: String) {
        viewModelScope.launch {
            deleteImageUrlAction.emit(imageUrl)
        }
    }
}