package com.example.travelplaner.accommodation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.db.AccommodationWithCity
import com.example.travelplaner.core.data.db.dao.AccommodationDao
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import com.example.travelplaner.destinations.AccommodationScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AccommodationViewModel @Inject constructor(
    appDataRepository: AppDataRepository,
    private val accommodationDao: AccommodationDao,
    savedStateHandle: SavedStateHandle,
    private val dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val _accommodation = MutableStateFlow<AccommodationWithCity?>(null)
    val accommodation = _accommodation.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    val editMode = _editMode.asStateFlow()

    private val imageUrlAction = MutableSharedFlow<String>()
    private val deleteImageUrlAction = MutableSharedFlow<String>()

    init {
        val navArgs = AccommodationScreenDestination.argsFrom(savedStateHandle)

        viewModelScope.launch {
            withContext(dispatchers.io) {
                accommodationDao.readByIdFlow(navArgs.accommodationId)
                    .collect { newAccommodation ->
                        _accommodation.update {
                            newAccommodation
                        }
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
                    val model = accommodationDao.readById(navArgs.accommodationId)
                    accommodationDao.update(
                        model.accommodation.copy(
                            additionalPhotos = model.accommodation.additionalPhotos + it
                        )
                    )
                }
        }

        viewModelScope.launch(dispatchers.io) {
            deleteImageUrlAction
                .collect {
                    val model = accommodationDao.readById(navArgs.accommodationId)
                    accommodationDao.update(
                        model.accommodation.copy(
                            additionalPhotos = model.accommodation.additionalPhotos - it
                        )
                    )
                }
        }
    }

    fun onToggleIsFavorite() {
        viewModelScope.launch(dispatchers.io) {
            val currentAccommodation = _accommodation.value!!.accommodation
            accommodationDao.update(
                currentAccommodation.copy(
                    isFavorite = !currentAccommodation.isFavorite
                )
            )
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