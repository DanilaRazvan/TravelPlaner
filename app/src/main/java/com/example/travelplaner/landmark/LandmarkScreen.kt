package com.example.travelplaner.landmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
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
import com.example.travelplaner.core.data.db.Landmark
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(
    navArgsDelegate = LandmarkScreenNavArgs::class
)
@Composable
fun LandmarkScreen(
    viewModel: LandmarkViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initial = null)

    LandmarkScreenContent(
        landmark = viewState,
        onBack = {
            navigator.navigateUp()
        }
    )
}

@Composable
private fun LandmarkScreenContent(
    landmark: Landmark?,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .navigationBarsPadding(),
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        landmark?.let {
            item {
                LandmarkImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((LocalConfiguration.current.screenHeightDp * 0.4).dp),
                    imageUrl = it.photoUrl,
                    onBackPressed = onBack
                )
            }
            item {
                LandmarkNameAndPrice(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    name = it.name,
                    price = it.ticketPrice
                )
            }
            item {
                LandmarkHours(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    hours = it.from + " - " + it.until,
                )
            }
            item {
                LandmarkDescription(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    description = it.description,
                )
            }
        }
    }
}

@Composable
private fun LandmarkImage(
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
private fun LandmarkNameAndPrice(
    modifier: Modifier = Modifier,
    name: String,
    price: Float
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
            text = "$price EUR",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LandmarkHours(
    modifier: Modifier = Modifier,
    hours: String,
) {
    Text(
        modifier = modifier,
        text = hours,
        style = MaterialTheme.typography.caption.copy(color = Color.White)
    )
}

@Composable
private fun LandmarkDescription(
    modifier: Modifier = Modifier,
    description: String,
) {
    Text(
        modifier = modifier,
        text = description,
        style = MaterialTheme.typography.caption.copy(color = Color.White)
    )
}