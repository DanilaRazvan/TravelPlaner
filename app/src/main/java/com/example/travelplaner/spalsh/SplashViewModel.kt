package com.example.travelplaner.spalsh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelplaner.core.data.AppDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appDataRepository: AppDataRepository
): ViewModel() {

    private val _loggedState = MutableStateFlow<Boolean?>(null)
    val loggedState = _loggedState.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedUser = appDataRepository.getLoggedUser()

            _loggedState.update {
                loggedUser != null
            }
        }
    }
}