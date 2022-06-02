package com.example.travelplaner.favorites

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.travelplaner.utils.asSimpleString
import com.example.travelplaner.utils.withDecimals
import java.util.*

@Composable
fun FavoritesScreenContent(
    viewState: FavoritesViewState,
    onTripSelected: (Long) -> Unit,
    onFlightSelected: (Long) -> Unit,
    onAccommodationSelected: (Long) -> Unit,
    onBackPressed: () -> Unit,
    onRemoveTripFromFavorites: (Long) -> Unit,
    onRemoveFlightFromFavorites: (Long) -> Unit,
    onRemoveAccommodationFromFavorites: (Long) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.surface,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                },
                title = { Text(text = "Favorites") },
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = Color.White,
                elevation = 0.dp
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colors.surface)
                    .navigationBarsPadding(),
                content = {
                    if (viewState.trips.isNotEmpty()) {
                        stickyHeader { CategoryHeader(name = "Visit") }
                    }
                    items(viewState.trips, key = { "trip_${it.id}" }) {
                        FavoritesListItem(
                            name = it.name,
                            totalPrice = if (it.totalPrice == 0f) "Free" else "${
                                it.totalPrice.toDouble().withDecimals(2)
                            } EUR",
                            onItemSelected = { onTripSelected(it.id) },
                            onRemoveFromFavorites = { onRemoveTripFromFavorites(it.id) }
                        )
                    }
                    if (viewState.flights.isNotEmpty()) {
                        stickyHeader { CategoryHeader(name = "Fly") }
                    }
                    items(viewState.flights, key = { "flight_${it.flight.id}" }) {
                        FavoritesListItem(
                            name = "${it.city.name}, ${it.city.country}",
                            totalPrice = it.flight.ticketPrice,
                            additionalInfo = Date(it.flight.from).asSimpleString() + " - " + Date(it.flight.to).asSimpleString(),
                            onItemSelected = { onFlightSelected(it.flight.id) },
                            onRemoveFromFavorites = { onRemoveFlightFromFavorites(it.flight.id) }
                        )
                    }
                    if (viewState.accommodations.isNotEmpty()) {
                        stickyHeader { CategoryHeader(name = "Sleep") }
                    }
                    items(viewState.accommodations, key = { "accommodation_${it.accommodation.id}" }) {
                        FavoritesListItem(
                            name = "${it.accommodation.name} - ${it.city.name}, ${it.city.country}",
                            totalPrice = "",
                            additionalInfo = Date(it.accommodation.from).asSimpleString() + " - " + Date(it.accommodation.to).asSimpleString(),
                            onItemSelected = { onAccommodationSelected(it.accommodation.id) },
                            onRemoveFromFavorites = { onRemoveAccommodationFromFavorites(it.accommodation.id) }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun FavoritesListItem(
    name: String,
    totalPrice: String,
    additionalInfo: String? = null,
    onItemSelected: () -> Unit,
    onRemoveFromFavorites: () -> Unit
) {

    val dismissState = rememberDismissState()
    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onRemoveFromFavorites()
    }

    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = {
            FractionalThreshold(0.2f)
        },
        background = {
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0f else 1f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    modifier = Modifier.scale(scale),
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Icon",
                )
            }
        },
        dismissContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clickable(onClick = onItemSelected)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = "$name ${if (totalPrice.isNotEmpty()) "- $totalPrice" else ""}",
                        style = MaterialTheme.typography.h5
                    )
                }

                additionalInfo?.let {
                    Text(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 24.dp),
                        text = additionalInfo,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}

@Composable
private fun CategoryHeader(
    modifier: Modifier = Modifier,
    name: String
) {
    Text(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 12.dp),
        text = name,
        style = MaterialTheme.typography.h4
    )
}