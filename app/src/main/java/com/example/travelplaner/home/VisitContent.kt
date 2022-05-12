package com.example.travelplaner.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.travelplaner.core.ui.components.TpPrimaryButton
import com.example.travelplaner.core.ui.components.TpTextField

@Composable
fun VisitContent(
    viewModel: HomeViewModel
) {
    val focusManager = LocalFocusManager.current

    var cityName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "City name",
            value = cityName,
            onValueChanged = { cityName = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Country",
            value = country,
            onValueChanged = { country = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Image URL",
            value = imageUrl,
            onValueChanged = { imageUrl = it },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Description",
            value = description,
            onValueChanged = { description = it },
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
                viewModel.onAddCity(
                    name = cityName,
                    country = country,
                    imageUrl = imageUrl,
                    description = description,
                )

                cityName = ""
                country = ""
                imageUrl = ""
                description = ""

                focusManager.clearFocus()
            }
        )
    }
}