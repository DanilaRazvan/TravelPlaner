package com.example.travelplaner.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.core.data.AppTheme
import com.example.travelplaner.core.ui.theme.ButtonShape
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initial = SettingsViewState())

    Scaffold(
        backgroundColor = MaterialTheme.colors.surface,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                },
                title = { Text(text = "Settings") },
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .padding(horizontal = 12.dp),
                    text = "Theme color",
                    style = MaterialTheme.typography.h6
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTheme.values().forEach { appTheme ->
                        ThemeCircle(
                            appTheme = appTheme,
                            isSelected = appTheme == state.selectedTheme,
                            onThemeSelected = viewModel::saveTheme
                        )
                    }
                }
            }
        }
    )

}

@Composable
private fun ThemeCircle(
    modifier: Modifier = Modifier,
    appTheme: AppTheme,
    isSelected: Boolean,
    onThemeSelected: (AppTheme) -> Unit
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(50))
            .background(
                color = when (appTheme) {
                    AppTheme.GREEN -> Color.Green
                    AppTheme.PURPLE -> Color(0xFFcc33ff)
                    AppTheme.ORANGE -> Color(0xFFff9933)
                    AppTheme.BLUE -> Color.Blue
                }
            )
            .border(
                width = 2.dp,
                color = if (isSelected) Color.White else Color.Gray,
                shape = RoundedCornerShape(50)
            )
            .clickable(onClick = { onThemeSelected(appTheme) })
    )
}