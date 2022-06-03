package com.example.travelplaner.landmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.travelplaner.core.data.db.Landmark
import com.example.travelplaner.core.ui.components.TpSecondaryButton
import com.example.travelplaner.core.ui.components.TpTextField
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.ui.theme.TextFieldShape
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.google.accompanist.pager.*
import com.google.android.material.math.MathUtils.lerp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.math.absoluteValue

@Destination(
    navArgsDelegate = LandmarkScreenNavArgs::class
)
@Composable
fun LandmarkScreen(
    viewModel: LandmarkViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initial = null)
    val editModeEnabled by viewModel.editMode.collectAsStateWithLifecycle(initial = false)

    var showAddPhotoDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LandmarkScreenContent(
            modifier = Modifier.fillMaxSize(),
            landmark = viewState,
            onBack = {
                navigator.navigateUp()
            },
            onDeleteImage = {
                if (editModeEnabled) viewModel.onDeleteImage(it)
            }
        )

        if (editModeEnabled) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .align(Alignment.BottomEnd),
                onClick = { showAddPhotoDialog = true },
                text = {
                    Text(text = "Photo")
                },
                icon = {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "add_photo")
                },
                contentColor = Color.White
            )
        }
    }

    if (showAddPhotoDialog) {
        var imageUrl by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current

        Dialog(
            onDismissRequest = { showAddPhotoDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .clip(TextFieldShape)
                    .background(MaterialTheme.colors.surface)
                    .padding(24.dp),
            ) {
                Text(
                    text = "New photo URL",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(8.dp))
                TpTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = imageUrl,
                    onValueChanged = { imageUrl = it },
                    labelText = "Image Url",
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    maxLines = 1,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                TpSecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Add",
                    onClick = { viewModel.addPhoto(imageUrl) },
                    enabled = true
                )
            }
        }
    }
}

@Composable
private fun LandmarkScreenContent(
    modifier: Modifier = Modifier,
    landmark: Landmark?,
    onBack: () -> Unit,
    onDeleteImage: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    name = it.name,
                    price = it.ticketPrice
                )
            }
            item {
                LandmarkHours(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    hours = it.from + " - " + it.until,
                )
            }
            item {
                LandmarkDescription(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    description = it.description,
                )
            }
            if (it.additionalPhotos.isNotEmpty()) {
                item {
                    AdditionalPhotos(
                        modifier = Modifier
                            .fillMaxWidth(),
                        photos = it.additionalPhotos,
                        onDeleteImage = onDeleteImage
                    )
                }
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun AdditionalPhotos(
    modifier: Modifier = Modifier,
    photos: List<String>,
    onDeleteImage: (String) -> Unit
) {
    val pagerState = rememberPagerState()

    Column(
        modifier = modifier
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            count = photos.size,
            state = pagerState,
        ) { page ->
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { onDeleteImage(photos[page]) }
                        )
                    }
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            0.85f,
                            1f,
                            1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            0.5f,
                            1f,
                            1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                painter = rememberImagePainter(
                    data = photos[page],
                    builder = {
                        crossfade(true)
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
            pagerState = pagerState
        )
    }
}
