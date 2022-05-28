package com.example.travelplaner.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.AppDataRepository
import com.example.travelplaner.core.data.AppTheme
import com.example.travelplaner.core.data.db.FlightWithCity
import com.example.travelplaner.core.di.AppCoroutineDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appDataRepository: AppDataRepository,
    private val dispatchers: AppCoroutineDispatchers
): ViewModel() {

    private val _state = MutableStateFlow(SettingsViewState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            merge(
                appDataRepository.getAppTheme()
            ).scan(
                initial = SettingsViewState()
            ) { oldState, event ->

                when (event) {
                    is AppTheme -> oldState.copy(
                        selectedTheme = event
                    )
                    else -> oldState
                }
            }.collect { newState ->
                _state.update { newState }
            }
        }
    }

    fun saveTheme(appTheme: AppTheme) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                appDataRepository.saveAppTheme(appTheme)
            }
        }
    }
}

data class SettingsViewState(
    val selectedTheme: AppTheme = AppTheme.GREEN
)