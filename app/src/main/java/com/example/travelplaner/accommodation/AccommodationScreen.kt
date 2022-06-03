package com.example.travelplaner.accommodation

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.travelplaner.core.ui.components.TpSecondaryButton
import com.example.travelplaner.core.ui.components.TpTextField
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.ui.theme.TextFieldShape
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.utils.asSimpleString
import com.google.accompanist.pager.*
import com.google.android.material.math.MathUtils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*
import kotlin.math.absoluteValue

@Destination(
    navArgsDelegate = AccommodationScreenNavArgs::class
)
@Composable
fun AccommodationScreen(
    navigator: DestinationsNavigator,
    viewModel: AccommodationViewModel = hiltViewModel()
) {

    val accommodation by viewModel.accommodation.collectAsStateWithLifecycle(initial = null)
    val editModeEnabled by viewModel.editMode.collectAsStateWithLifecycle(initial = false)

    var showAddPhotoDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.surface,
        floatingActionButton = {
            if (!editModeEnabled) {
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
            } else {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(16.dp).navigationBarsPadding(),
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
            if (accommodation?.accommodation?.additionalPhotos?.isEmpty() == false) {
                item {
                    AdditionalPhotos(
                        modifier = Modifier
                            .fillMaxWidth(),
                        photos = accommodation?.accommodation?.additionalPhotos.orEmpty(),
                        onDeleteImage = {
                            if (editModeEnabled) viewModel.onDeleteImage(it)
                        }
                    )
                }
            }
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
                        MathUtils.lerp(
                            0.85f,
                            1f,
                            1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = MathUtils.lerp(
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