package com.example.travelplaner.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.travelplaner.core.data.AppTheme
import com.example.travelplaner.core.data.PREFERENCE_APP_THEME
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.dataStore

val BottomSheetShape = RoundedCornerShape(
    topStart = 48.dp,
    topEnd = 48.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

val tp_divider_color = Color.LightGray
private val tp_red = Color(0xFFFE6D70)
private val tp_white = Color.White
private val tp_green_700 = Color(0xFF129178)
private val tp_green_800 = Color(0xFF067560)
private val tp_green_900 = Color(0xFF045848)
val TpGreenColors = lightColors(
    primary = tp_green_800,
    secondary = tp_red,
    surface = tp_green_900,
    onSurface = tp_white,
    primaryVariant = tp_green_700,
    error = tp_red,
)
private val tp_purple_700 = Color(0xFFb300b3)
private val tp_purple_800 = Color(0xFF800080)
private val tp_purple_900 = Color(0xFF4d004d)
val TpPurpleColors = lightColors(
    primary = tp_purple_800,
    secondary = tp_red,
    surface = tp_purple_900,
    onSurface = tp_white,
    primaryVariant = tp_purple_700,
    error = tp_red,
)
private val tp_blue_700 = Color(0xFF0000e6)
private val tp_blue_800 = Color(0xFF000099)
private val tp_blue_900 = Color(0xFF000066)
val TpBlueColors = lightColors(
    primary = tp_blue_800,
    secondary = tp_red,
    surface = tp_blue_900,
    onSurface = tp_white,
    primaryVariant = tp_blue_700,
    error = tp_red,
)
private val tp_orange_700 = Color(0xFFe65c00)
private val tp_orange_800 = Color(0xFFb34700)
private val tp_orange_900 = Color(0xFF803300)
val TpOrangeColors = lightColors(
    primary = tp_orange_800,
    secondary = tp_red,
    surface = tp_orange_900,
    onSurface = tp_white,
    primaryVariant = tp_orange_700,
    error = tp_red,
)

val ButtonShape = RoundedCornerShape(50)
val TextFieldShape = RoundedCornerShape(28.dp)

@Composable
fun TravelPlanerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeState = context.dataStore.data.collectAsStateWithLifecycle(initial = null)

    val theme = themeState.value?.let { preference ->
        when (preference[PREFERENCE_APP_THEME]) {
            AppTheme.GREEN.name -> AppTheme.GREEN
            AppTheme.PURPLE.name -> AppTheme.PURPLE
            AppTheme.ORANGE.name -> AppTheme.ORANGE
            AppTheme.BLUE.name -> AppTheme.BLUE
            else -> AppTheme.GREEN
        }
    } ?: AppTheme.GREEN

    val colors = when (theme) {
        AppTheme.GREEN -> TpGreenColors
        AppTheme.PURPLE -> TpPurpleColors
        AppTheme.ORANGE -> TpOrangeColors
        AppTheme.BLUE -> TpBlueColors
    }

    MaterialTheme(
        colors = colors,
        typography = TpTypography,
        content = content
    )
}