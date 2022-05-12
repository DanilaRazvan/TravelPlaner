package com.example.travelplaner.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.NavGraphs
import com.example.travelplaner.R
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.FavoritesScreenDestination
import com.example.travelplaner.destinations.TripDetailsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initial = HomeViewState())

    val context = LocalContext.current
    HomeScreenContent(
        viewState = viewState,
        onFavoritesClicked = {
            navigator.navigate(FavoritesScreenDestination.route)
        },
        onSettingsClicked = {
            Toast.makeText(
                context,
                context.getString(R.string.not_supported),
                Toast.LENGTH_SHORT
            ).show()
        },
        onSearchTextChanged = viewModel.onSearchTextChanged,
        onScreenChanged = viewModel.onScreenChanged,
        onSearchByDestination = viewModel.onSearchByDestination,
        onLogout = {
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
        viewModel = viewModel
    )
}