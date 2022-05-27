package com.example.travelplaner.accommodation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .navigationBarsPadding(),
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AccommodationImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((LocalConfiguration.current.screenHeightDp * 0.4).dp),
                imageUrl = accommodation?.accommodation?.photoUrl.orEmpty(),
                onBackPressed = { navigator.navigateUp() }
            )
        }
        item {
            AccommodationCityAndPrice(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                city = accommodation?.city?.name.orEmpty(),
                price = ""
            )
        }
        item {
            AccommodationDescription(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                description = Date(accommodation?.accommodation?.from ?: 0L).asSimpleString() + " - " + Date(accommodation?.accommodation?.to ?: 0L).asSimpleString(),
            )
        }
        item {
            AccommodationDescription(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                description = accommodation?.accommodation?.description.orEmpty(),
            )
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
                .size(32.dp)
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
    city: String,
    price: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.weight(.6f),
            text = "Flight to $city",
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

