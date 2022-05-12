package com.example.travelplaner.core.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.travelplaner.R
import com.example.travelplaner.core.ui.theme.ButtonShape

@Composable
fun TpSecondaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val colors = ButtonDefaults.textButtonColors(
        contentColor = Color.White,
        disabledContentColor = Color.White.copy(alpha = .7f)
    )
    TextButton(
        modifier = modifier
            .height(dimensionResource(id = R.dimen.button_height)),
        onClick = onClick,
        shape = ButtonShape,
        enabled = enabled,
        colors = colors
    ) {
        Text(text = text)
    }
}