package com.example.travelplaner.home

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.travelplaner.core.ui.components.TpPrimaryButton
import com.example.travelplaner.core.ui.components.TpTextField
import com.example.travelplaner.core.ui.model.TpListItemUiModel
import com.example.travelplaner.utils.asSimpleString
import java.util.*

@Composable
fun SleepContent(
    viewModel: HomeViewModel
) {
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current
    val cities by remember { mutableStateOf(viewModel.viewState.value.cities) }

    var selectedCity by remember { mutableStateOf<TpListItemUiModel?>(null) }
    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var from by remember { mutableStateOf(Date()) }
    var to by remember { mutableStateOf(Date()) }
    val fromCalendar = remember { Calendar.getInstance() }
    fromCalendar.time = from
    val toCalendar = remember { Calendar.getInstance() }
    toCalendar.time = to
    val fromDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                fromCalendar.set(year, month, day)
                from = fromCalendar.time
            },
            fromCalendar.get(Calendar.YEAR),
            fromCalendar.get(Calendar.MONTH),
            fromCalendar.get(Calendar.DAY_OF_MONTH),
        )
    }
    val toDatePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                toCalendar.set(year, month, day)
                to = toCalendar.time
            },
            toCalendar.get(Calendar.YEAR),
            toCalendar.get(Calendar.MONTH),
            toCalendar.get(Calendar.DAY_OF_MONTH),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded && cities.isNotEmpty(),
            onExpandedChange = { expanded = !expanded }
        ) {
            TpTextField(
                modifier = Modifier.fillMaxWidth(),
                labelText = "City",
                value = selectedCity?.city.orEmpty(),
                onValueChanged = { },
                maxLines = 1,
                singleLine = true,
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded && cities.isNotEmpty()
                    )
                }
            )

            ExposedDropdownMenu(
                expanded = expanded && cities.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        onClick = {
                            selectedCity = city
                            expanded = false
                        }
                    ) {
                        Text(text = city.city + ", " + city.country)
                    }
                }
            }
        }
        TpTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        fromDatePickerDialog.show()
                    }
                ),
            labelText = "From",
            value = from.asSimpleString(),
            readOnly = true,
            enabled = false,
            singleLine = true,
            maxLines = 1,
            trailingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "calendar", tint = Color.White)
            }
        )
        TpTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        toDatePickerDialog.show()
                    }
                ),
            labelText = "To",
            value = to.asSimpleString(),
            readOnly = true,
            enabled = false,
            singleLine = true,
            maxLines = 1,
            trailingIcon = {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "calendar", tint = Color.White)
            }
        )
        TpTextField(
            modifier = Modifier.fillMaxWidth(),
            labelText = "Name",
            value = name,
            onValueChanged = { name = it },
            singleLine = true,
            maxLines = 1,
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
            singleLine = true,
            maxLines = 1,
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
            singleLine = true,
            maxLines = 1,
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
                viewModel.onAddAccommodation(
                    cityId = selectedCity!!.id,
                    name = name,
                    imageUrl = imageUrl,
                    description = description,
                    from = fromCalendar.timeInMillis,
                    to = toCalendar.timeInMillis,
                )

                selectedCity = null
                name = ""
                imageUrl = ""
                description = ""

                focusManager.clearFocus()
            }
        )
    }
}