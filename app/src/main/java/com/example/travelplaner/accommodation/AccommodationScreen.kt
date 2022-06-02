package com.example.travelplaner.accommodation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.utils.asSimpleString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@Destination(
    navArgsDelegate = AccommodationScreenNavArgs::class
)
@Composable
fun AccommodationScreen(
    navigator: DestinationsNavigator,
    viewModel: AccommodationViewModel = hiltViewModel()
) {

    val accommodation by viewModel.accommodation.collectAsStateWithLifecycle(initial = null)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.surface,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = { viewModel.onToggleIsFavorite() }
            ) {
                Icon(
                    imageVector = if (accommodation?.accommodation?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "fav",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colors.surface)
                .navigationBarsPadding(),
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AccommodationImage(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height((LocalConfiguration.current.screenHeightDp * 0.4).dp),
                    imageUrl = accommodation?.accommodation?.photoUrl.orEmpty(),
                    onBackPressed = { navigator.navigateUp() }
                )
            }
            item {
                AccommodationCityAndPrice(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 24.dp),
                    name = accommodation?.accommodation?.name.orEmpty(),
                    price = ""
                )
            }
            item {
                AccommodationDescription(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 24.dp),
                    description = Date(accommodation?.accommodation?.from ?: 0L).asSimpleString() + " - " + Date(accommodation?.accommodation?.to ?: 0L).asSimpleString(),
                )
            }
            item {
                AccommodationDescription(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 24.dp),
                    description = accommodation?.accommodation?.description.orEmpty(),
                )
            }
        }
    }
}

@Composable
private fun AccommodationImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onBackPressed: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = rememberImagePainter(
                data = imageUrl,
                builder = {
                    crossfade(true)
                }
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp)
                .size(48.dp)
                .clip(ButtonShape)
                .background(MaterialTheme.colors.surface)
                .clickable {
                    onBackPressed()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "back",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun AccommodationCityAndPrice(
    modifier: Modifier = Modifier,
    name: String,
    price: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(.6f),
            text = name,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.weight(.4f),
            text = price,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AccommodationDescription(
    modifier: Modifier = Modifier,
    description: String
) {
    Text(
        modifier = modifier,
        text = description,
        style = MaterialTheme.typography.caption.copy(color = Color.White)
    )
}

