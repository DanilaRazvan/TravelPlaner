package com.example.travelplaner.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.NavGraphs
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initial = HomeViewState())

    HomeScreenContent(
        viewState = viewState,
        onFavoritesClicked = {
            navigator.navigate(FavoritesScreenDestination.route)
        },
        onSettingsClicked = {
            val route = SettingsScreenDestination().route
            navigator.navigate(route)
        },
        onSearchTextChanged = viewModel.onSearchTextChanged,
        onScreenChanged = viewModel.onScreenChanged,
        onSearchByDestination = viewModel.onSearchByDestination,
        onLogout = {
            viewModel.onLogout()
            navigator.navigate(NavGraphs.root.route) {
                this.popUpTo(NavGraphs.root.route) {
                    this.inclusive = false
                }
            }
        },
        onToggleEditMode = viewModel.onToggleEditMode,
        onDestinationSelected = { cityId ->
            val route = TripDetailsScreenDestination(
                cityId = cityId
            ).route
            navigator.navigate(route)
        },
        onFlightSelected = { flightId ->
            val route = FlightScreenDestination(
                flightId = flightId
            ).route
            navigator.navigate(route)
        },
        onAccommodationSelected = { accommodationId ->
            val route = AccommodationScreenDestination(
                accommodationId = accommodationId
            ).route
            navigator.navigate(route)
        },
        viewModel = viewModel
    )
}