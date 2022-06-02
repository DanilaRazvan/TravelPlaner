package com.example.travelplaner.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.AccommodationScreenDestination
import com.example.travelplaner.destinations.FlightScreenDestination
import com.example.travelplaner.destinations.TripDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FavoritesScreen(
    navigator: DestinationsNavigator,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(FavoritesViewState())

    FavoritesScreenContent(
        viewState = viewState,
        onTripSelected = { tripId ->
            val route = TripDetailsScreenDestination(
                tripId = tripId
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
        onBackPressed = {
            navigator.navigateUp()
        },
        onRemoveTripFromFavorites = { tripId ->
            viewModel.removeTripFromFavorites(tripId)
        },
        onRemoveFlightFromFavorites = { flightId ->
            viewModel.removeFlightFromFavorites(flightId)
        },
        onRemoveAccommodationFromFavorites = { accommodationId ->
            viewModel.removeAccommodationFromFavorites(accommodationId)
        }
    )
}