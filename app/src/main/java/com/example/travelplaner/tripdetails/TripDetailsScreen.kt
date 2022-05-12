package com.example.travelplaner.tripdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.LandmarkScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(
    navArgsDelegate = TripDetailsScreenNavArgs::class
)
@Composable
fun TripDetailsScreen(
    viewModel: TripDetailsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val viewState by viewModel.viewState.collectAsStateWithLifecycle(TripDetailsViewState())

    LaunchedEffect(key1 = viewState.tripSaved, block = {
        if (viewState.tripSaved) {
            navigator.navigateUp()
        }
    })

    TripDetailsContent(
        viewState = viewState,
        viewModel = viewModel,
        onBackPressed = {
            navigator.navigateUp()
        },
        onLandmarkSelected = { landmarkId ->
            navigator.navigate(
                LandmarkScreenDestination(
                    landmarkId = landmarkId
                ).route
            )
        }
    )
}