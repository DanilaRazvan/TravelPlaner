package com.example.travelplaner.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.example.travelplaner.R
import com.example.travelplaner.core.ui.components.TpTextField
import com.example.travelplaner.core.ui.model.TpListItemUiModel
import com.example.travelplaner.core.ui.theme.BottomSheetShape
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.ui.theme.tp_divider_color
import kotlinx.coroutines.launch

private enum class DrawerEntry {
    Favorites, Settings
}

@Composable
fun HomeScreenContent(
    viewState: HomeViewState,
    onSettingsClicked: () -> Unit,
    onFavoritesClicked: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onScreenChanged: (HomeScreen) -> Unit,
    onSearchByDestination: () -> Unit,
    onLogout: () -> Unit,
    onToggleEditMode: () -> Unit,
    onDestinationSelected: (Long) -> Unit,
    viewModel: HomeViewModel
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = !viewState.isLoading,
        drawerContent = {
            HomeDrawer(
                modifier = Modifier.systemBarsPadding(),
                entries = DrawerEntry.values().toList(),
                onEntrySelected = {
                    when (it) {
                        DrawerEntry.Favorites -> onFavoritesClicked()
                        DrawerEntry.Settings -> onSettingsClicked()
                    }
                },
                onLogout = onLogout,
                onToggleEditMode = onToggleEditMode
            )
        },
        content = { contentPadding ->
            val scope = rememberCoroutineScope()
            HomeContent(
                modifier = Modifier.padding(contentPadding),
                isLoading = viewState.isLoading,
                searchText = viewState.searchText,
                onSearchTextChanged = onSearchTextChanged,
                selectedScreen = viewState.selectedScreen,
                onScreenChanged = onScreenChanged,
                listModels = when (viewState.selectedScreen) {
                    HomeScreen.Visit -> viewState.cities
                    HomeScreen.Fly -> viewState.flights
                    HomeScreen.Sleep -> viewState.accommodations
                },
                openDrawer = {
                    if (!viewState.isLoading) {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }
                },
                onSearchByDestination = onSearchByDestination,
                isEditModeEnabled = viewState.isEditModeEnabled,
                onDestinationSelected = onDestinationSelected,
                viewModel = viewModel
            )
        }
    )
}

@Composable
private fun HomeDrawer(
    modifier: Modifier = Modifier,
    entries: List<DrawerEntry>,
    onEntrySelected: (DrawerEntry) -> Unit,
    onLogout: () -> Unit,
    onToggleEditMode: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .height(256.dp)
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onToggleEditMode() }
                    )
                },
            painter = painterResource(R.drawable.ic_launcher_logo_foreground),
            contentDescription = "logo"
        )

        Column(modifier = Modifier.weight(1f)) {
            entries.forEach { drawerEntry ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ButtonShape)
                        .clickable {
                            onEntrySelected(drawerEntry)
                        }
                        .padding(vertical = 8.dp),
                    text = drawerEntry.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4
                )
            }
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ButtonShape)
                .clickable { onLogout() }
                .padding(vertical = 8.dp),
            text = "Log out",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    selectedScreen: HomeScreen,
    onScreenChanged: (HomeScreen) -> Unit,
    listModels: List<TpListItemUiModel>,
    openDrawer: () -> Unit,
    onSearchByDestination: () -> Unit,
    isEditModeEnabled: Boolean,
    onDestinationSelected: (Long) -> Unit,
    viewModel: HomeViewModel
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        BackdropScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed),
            appBar = {
                HomeTabBar(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .statusBarsPadding(),
                    openDrawer = openDrawer,
                    selectedTab = selectedScreen,
                    onTabSelected = {
                        if (!isLoading) {
                            onScreenChanged(it)
                        }
                    },
                )
            },
            gesturesEnabled = !isLoading,
            backLayerBackgroundColor = MaterialTheme.colors.primaryVariant,
            backLayerContent = {
                if (isEditModeEnabled) {
                    DeveloperBackLayerContent(
                        selectedTab = selectedScreen,
                        viewModel = viewModel
                    )
                } else {
                    BackLayerContent(
                        isLoading = isLoading,
                        searchText = searchText,
                        onSearchTextChanged = onSearchTextChanged,
                        onSearchByDestination = onSearchByDestination,
                        focusManager = focusManager
                    )
                }
            },
            peekHeight = BackdropScaffoldDefaults.PeekHeight + WindowInsets.statusBars.asPaddingValues()
                .calculateTopPadding(),
            frontLayerScrimColor = Color.Unspecified,
            frontLayerShape = BottomSheetShape,
            frontLayerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(BottomSheetShape)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        ExploreSection(
                            isLoading = isLoading,
                            title = if (isEditModeEnabled) "DEVELOPER MODE ENABLED" else stringResource(
                                R.string.explore_by_destination
                            ),
                            exploreList = listModels,
                            isEditModeEnabled = isEditModeEnabled,
                            onRemoveElement = { elementId ->
                                viewModel.onRemoveElement(elementId, selectedScreen)
                            },
                            onDestinationSelected = {
                                if (selectedScreen == HomeScreen.Visit) onDestinationSelected(it.id)
                            },
                        )
                    }
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
private fun DeveloperBackLayerContent(
    selectedTab: HomeScreen,
    viewModel: HomeViewModel
) {
    when (selectedTab) {
        HomeScreen.Visit -> VisitContent(viewModel)
        HomeScreen.Fly -> FlyContent(viewModel)
        HomeScreen.Sleep -> SleepContent(viewModel)
    }
}

@Composable
private fun BackLayerContent(
    isLoading: Boolean,
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchByDestination: () -> Unit,
    focusManager: FocusManager
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            value = searchText,
            onValueChanged = onSearchTextChanged,
            labelText = stringResource(id = R.string.search_destination_label),
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchByDestination()
                    focusManager.clearFocus()
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onSearchTextChanged("")
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear"
                    )
                }
            }
        )
    }
}

@Composable
private fun ExploreSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    title: String,
    exploreList: List<TpListItemUiModel>,
    isEditModeEnabled: Boolean,
    onDestinationSelected: (TpListItemUiModel) -> Unit,
    onRemoveElement: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .padding(top = 12.dp)
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .clip(RoundedCornerShape(50f))
                    .fillMaxWidth(.5f),
                color = Color.White
            )
        } else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption.copy(color = Color.White)
            )
        }
        Spacer(Modifier.height(12.dp))

        if (!isLoading) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                items(
                    items = exploreList,
                    key = { item -> item.id }
                ) { exploreItem ->
                    if (isEditModeEnabled) {
                        val dismissState = rememberDismissState()
                        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                            onRemoveElement(exploreItem.id)
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
                                        onDestinationSelected(exploreItem)
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
                                onDestinationSelected(exploreItem)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreListItem(
    modifier: Modifier = Modifier,
    exploreItem: TpListItemUiModel,
    onDestinationSelected: (TpListItemUiModel) -> Unit = { }
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
    item: TpListItemUiModel,
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
                text = item.city + if (item.country.isNotBlank()) ", ${item.country}" else "",
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
private fun HomeTabBar(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    selectedTab: HomeScreen,
    onTabSelected: (HomeScreen) -> Unit
) {
    TpTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        TpTabs(
            modifier = tabBarModifier,
            titles = HomeScreen.values().map { it.name },
            selectedTab = selectedTab,
            onTabSelected = { newTab -> onTabSelected(HomeScreen.values()[newTab.ordinal]) }
        )
    }
}

@Composable
private fun TpTabBar(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit,
    children: @Composable (Modifier) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClicked) {
            Icon(
                imageVector = Icons.Default.Menu,
                tint = Color.White,
                contentDescription = "menu"
            )
        }

        children(
            Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TpTabs(
    modifier: Modifier = Modifier,
    titles: List<String>,
    selectedTab: HomeScreen,
    onTabSelected: (HomeScreen) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier,
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        indicator = { tabPositions: List<TabPosition> ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                    .fillMaxSize()
                    .border(BorderStroke(2.dp, Color.White), RoundedCornerShape(16.dp))
            )
        },
        divider = { }
    ) {
        titles.forEachIndexed { index, title ->
            val selected = index == selectedTab.ordinal

            val textModifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)

            Tab(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                selected = selected,
                onClick = {
                    onTabSelected(HomeScreen.values()[index])
                }
            ) {
                Text(
                    modifier = textModifier,
                    text = title.uppercase()
                )
            }
        }
    }
}