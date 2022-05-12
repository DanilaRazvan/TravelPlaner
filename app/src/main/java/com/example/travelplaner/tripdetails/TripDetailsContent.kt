package com.example.travelplaner.tripdetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.travelplaner.R
import com.example.travelplaner.core.ui.components.TpPrimaryButton
import com.example.travelplaner.core.ui.components.TpTextField
import com.example.travelplaner.core.ui.model.LandmarkUiModel
import com.example.travelplaner.core.ui.theme.BottomSheetShape
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.ui.theme.tp_divider_color

@Composable
fun TripDetailsContent(
    viewState: TripDetailsViewState,
    viewModel: TripDetailsViewModel,
    onBackPressed: () -> Unit,
    onLandmarkSelected: (Long) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        BackdropScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed),
            backLayerBackgroundColor = MaterialTheme.colors.primaryVariant,
            appBar = {
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
                    title = { Text(text = viewState.destination) },
                    actions = {
                        if (viewState.showSaveFavoriteIcon) {
                            IconButton(
                                onClick = viewModel::onSaveTrip
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "favorite",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    contentColor = Color.White,
                    backgroundColor = MaterialTheme.colors.primaryVariant,
                    elevation = 0.dp
                )
            },
            backLayerContent = {
                TripDetailsHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primaryVariant),
                    totalPrice = "${viewState.totalPrice} EUR",
                    isEditModeEnabled = viewState.isEditModeEnabled,
                    viewModel = viewModel
                )
            },
            peekHeight = BackdropScaffoldDefaults.PeekHeight + WindowInsets.statusBars.asPaddingValues()
                .calculateTopPadding(),
            frontLayerScrimColor = Color.Unspecified,
            frontLayerShape = BottomSheetShape,
            frontLayerContent = {
                ExploreSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(BottomSheetShape)
                        .background(MaterialTheme.colors.surface)
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(vertical = 24.dp, horizontal = 12.dp),
                    landmarks = viewState.landmarks,
                    isEditModeEnabled = viewState.isEditModeEnabled,
                    onRemoveElement = viewModel::removeLandmark,
                    onExcludeFromTrip = viewModel::excludeFromTrip,
                    gesturesEnabled = viewState.canRemoveLandmarks,
                    onLandmarkSelected = onLandmarkSelected
                )
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .background(MaterialTheme.colors.surface)
        )
    }
}

@Composable
private fun TripDetailsHeader(
    modifier: Modifier = Modifier,
    totalPrice: String,
    isEditModeEnabled: Boolean,
    viewModel: TripDetailsViewModel
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isEditModeEnabled) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Total price: $totalPrice",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h6
            )
        } else {
            LandmarkInfoTextFields(viewModel)
        }
    }
}

@Composable
private fun ExploreSection(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    landmarks: List<LandmarkUiModel>,
    isEditModeEnabled: Boolean,
    onRemoveElement: (Long) -> Unit,
    onExcludeFromTrip: (LandmarkUiModel) -> Unit,
    gesturesEnabled: Boolean,
    onLandmarkSelected: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        content = {
            items(
                items = landmarks,
                key = { item -> item.id }
            ) { exploreItem ->

                if (gesturesEnabled) {
                    val dismissState = rememberDismissState()
                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        if (isEditModeEnabled)
                            onRemoveElement(exploreItem.id)
                        else
                            onExcludeFromTrip(exploreItem)
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
                                modifier = Modifier.fillMaxSize(),
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
                            ExploreListItem(
                                modifier = Modifier.fillParentMaxWidth(),
                                exploreItem = exploreItem,
                                onDestinationSelected = {
                                    onLandmarkSelected(it.id)
                                }
                            )
                        },
                        directions = setOf(DismissDirection.EndToStart)
                    )
                } else {
                    ExploreListItem(
                        modifier = Modifier.fillParentMaxWidth(),
                        exploreItem = exploreItem,
                        onDestinationSelected = {
                            onLandmarkSelected(it.id)
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun ExploreListItem(
    modifier: Modifier = Modifier,
    exploreItem: LandmarkUiModel,
    onDestinationSelected: (LandmarkUiModel) -> Unit = { }
) {
    Column(
        modifier = modifier
    ) {
        ExploreItem(
            modifier = Modifier.fillMaxWidth(),
            item = exploreItem,
            onDestinationSelected = { onDestinationSelected(exploreItem) }
        )
        Divider(color = tp_divider_color)
    }
}

@Composable
private fun ExploreItem(
    modifier: Modifier = Modifier,
    onDestinationSelected: () -> Unit,
    item: LandmarkUiModel,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onDestinationSelected)
            .padding(top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            val painter = rememberImagePainter(
                data = item.imageUrl,
                builder = {
                    crossfade(true)
                }
            )
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(ButtonShape),
            )

            if (painter.state is ImagePainter.State.Loading) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_logo_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                )
            }
        }

        Spacer(Modifier.width(24.dp))

        Column {
            Text(
                text = item.name + " - ${if (item.price == 0f) "Free" else "${item.price} EUR"}",
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.details,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun LandmarkInfoTextFields(
    viewModel: TripDetailsViewModel
) {
    val focusManager = LocalFocusManager.current

    var landmarkName by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }
    var landmarkDescription by remember { mutableStateOf("") }
    var landmarkPhotoUrl by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("") }
    var until by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Landmark name",
            value = landmarkName,
            onValueChanged = { landmarkName = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Ticket Price",
            value = ticketPrice,
            onValueChanged = { ticketPrice = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "From",
            value = from,
            onValueChanged = { from = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Until",
            value = until,
            onValueChanged = { until = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Image URL",
            value = landmarkPhotoUrl,
            onValueChanged = { landmarkPhotoUrl = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )

        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Description",
            value = landmarkDescription,
            onValueChanged = { landmarkDescription = it },
            maxLines = 5,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus(true) }
            )
        )

        TpPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Save",
            onClick = {

                viewModel.onAddLandmark(
                    landmarkName = landmarkName,
                    ticketPrice = ticketPrice,
                    imageUrl = landmarkPhotoUrl,
                    from = from,
                    until = until,
                    description = landmarkDescription
                )

                landmarkName = ""
                ticketPrice = ""
                landmarkPhotoUrl = ""
                landmarkDescription = ""
                from = ""
                until = ""

                focusManager.clearFocus()
            }
        )
    }
}

