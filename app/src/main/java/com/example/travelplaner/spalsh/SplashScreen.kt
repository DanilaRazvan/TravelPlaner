package com.example.travelplaner.spalsh

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.HomeScreenDestination
import com.example.travelplaner.destinations.LoginScreenDestination
import com.example.travelplaner.login.AppLogo
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(
    start = true
)
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator,
    viewModel: SplashViewModel = hiltViewModel()
) {

    val loggedState by viewModel.loggedState.collectAsStateWithLifecycle(initial = null)
    LaunchedEffect(
        key1 = loggedState,
        block = {
            loggedState?.let {
                val route =
                    if (it) HomeScreenDestination().route else LoginScreenDestination().route
                navigator.navigate(route)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    )
}