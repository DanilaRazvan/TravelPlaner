package com.example.travelplaner.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.travelplaner.R
import com.example.travelplaner.core.ui.theme.TextFieldShape

@Composable
fun TpTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit = {},
    maxLines: Int = Int.MAX_VALUE,
    singleLine: Boolean = false,
    readOnly: Boolean = false,
    errorMessage: String? = null,
    labelText: String? = null,
    enabled: Boolean = true,
    placeholderText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: (@Composable () -> Unit)? = null
) {

    val labelComposable: (@Composable () -> Unit)? = labelText?.let {
        @Composable {
            Text(text = it)
        }
    }
    val placeholderComposable: (@Composable () -> Unit)? = placeholderText?.let {
        @Composable {
            Text(text = it)
        }
    }

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = Color.White,
        disabledTextColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White,
        disabledBorderColor = Color.White,
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White,
        disabledLabelColor = Color.White,
        cursorColor = Color.White,
    )

    Column {
        OutlinedTextField(
            modifier = modifier
                .heightIn(
                    min = dimensionResource(id = R.dimen.text_field_height)
                ),
            colors = colors,
            value = value,
            maxLines = maxLines,
            singleLine = singleLine,
            readOnly = readOnly,
            onValueChange = onValueChanged,
            label = labelComposable,
            placeholder = placeholderComposable,
            shape = TextFieldShape,
            isError = errorMessage != null,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            trailingIcon = trailingIcon
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                modifier = Modifier
                    .padding(
                        top = 4.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
            )
        }
    }
}