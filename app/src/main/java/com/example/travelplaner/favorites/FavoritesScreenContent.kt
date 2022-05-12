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
import com.example.travelplaner.core.data.db.Trip
import com.example.travelplaner.utils.withDecimals

@Composable
fun FavoritesScreenContent(
    viewState: FavoritesViewState,
    onTripSelected: (Long) -> Unit,
    onBackPressed: () -> Unit,
    onRemoveFromFavorites: (Long) -> Unit
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
                    items(viewState.trips, key = { it.id }) {
                        FavoritesListItem(
                            trip = it,
                            onItemSelected = { onTripSelected(it.id) },
                            onRemoveFromFavorites = { onRemoveFromFavorites(it.id) }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun FavoritesListItem(
    trip: Trip,
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
                modifier = Modifier.fillMaxSize().padding(end = 24.dp),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onItemSelected)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "${trip.name} - ${
                        if (trip.totalPrice == 0f) "Free" else "${
                            trip.totalPrice.toDouble().withDecimals(2)
                        } EUR"
                    }",
                    style = MaterialTheme.typography.h4
                )
            }
        },
        directions = setOf(DismissDirection.EndToStart)
    )
}