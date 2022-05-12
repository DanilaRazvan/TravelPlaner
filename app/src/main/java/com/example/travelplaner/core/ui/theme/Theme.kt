package com.example.travelplaner.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val BottomSheetShape = RoundedCornerShape(
    topStart = 48.dp,
    topEnd = 48.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

val tp_divider_color = Color.LightGray
private val tp_red = Color(0xFFFE6D70)
private val tp_white = Color.White
private val tp_purple_700 = Color(0xFF129178)
private val tp_purple_800 = Color(0xFF067560)
private val tp_purple_900 = Color(0xFF045848)
val TpColors = lightColors(
    primary = tp_purple_800,
    secondary = tp_red,
    surface = tp_purple_900,
    onSurface = tp_white,
    primaryVariant = tp_purple_700,
    error = tp_red,
)

val ButtonShape = RoundedCornerShape(50)
val TextFieldShape = RoundedCornerShape(28.dp)

@Composable
fun TravelPlanerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = TpColors,
        typography = TpTypography,
        content = content
    )
}