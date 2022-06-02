package com.example.travelplaner.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.travelplaner.R
import com.example.travelplaner.core.ui.components.TpPrimaryButton
import com.example.travelplaner.core.ui.components.TpSecondaryButton
import com.example.travelplaner.core.ui.components.TpTextField

@Composable
fun LoginScreenContent(
    viewState: LoginViewState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    focusManager.clearFocus(true)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppLogo(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.4f)
        )

        if (viewState is LoginViewState.SubmissionError) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = viewState.errorMessage.asString(),
                color = MaterialTheme.colors.error,
            )
        }

        ActionsArea(
            modifier = Modifier
                .fillMaxWidth()
                .weight(.6f),
            viewState = viewState,
            onUsernameChanged = onUsernameChanged,
            onPasswordChanged = onPasswordChanged,
            onLoginClicked = {
                focusManager.clearFocus()
                onLoginClicked()
            },
            onSignUpClicked = {
                focusManager.clearFocus()
                onSignUpClicked()
            },
            focusManager = focusManager
        )
    }
}

@Composable
fun AppLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(1f),
            painter = painterResource(id = R.drawable.ic_launcher_logo_foreground),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ActionsArea(
    modifier: Modifier = Modifier,
    viewState: LoginViewState,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    focusManager: FocusManager,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        UsernameInput(
            text = viewState.credentials.username.value,
            onTextChanged = onUsernameChanged,
            errorMessage = (viewState as? LoginViewState.Active)?.usernameError?.asString(),
            enabled = viewState.inputsEnabled
        )
        PasswordInput(
            text = viewState.credentials.password.value,
            onTextChanged = onPasswordChanged,
            errorMessage = (viewState as? LoginViewState.Active)?.passwordError?.asString(),
            enabled = viewState.inputsEnabled,
            focusManager = focusManager
        )
        Spacer(modifier = Modifier.height(12.dp))
        LoginButton(
            onClick = onLoginClicked,
            enabled = viewState.inputsEnabled
        )
        SignUpButton(
            onClick = onSignUpClicked,
            enabled = viewState.inputsEnabled
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun UsernameInput(
    text: String,
    onTextChanged: (String) -> Unit,
    errorMessage: String?,
    enabled: Boolean
) {
    TpTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChanged = onTextChanged,
        labelText = stringResource(R.string.username),
        errorMessage = errorMessage,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
    )
}

@Composable
fun PasswordInput(
    text: String,
    onTextChanged: (String) -> Unit,
    errorMessage: String?,
    enabled: Boolean,
    focusManager: FocusManager
) {
    TpTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChanged = onTextChanged,
        labelText = stringResource(R.string.password),
        errorMessage = errorMessage,
        enabled = enabled,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus(true)
            }
        )
    )
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    TpPrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.log_in),
        onClick = onClick,
        enabled = enabled
    )
}

@Composable
fun SignUpButton(
    onClick: () -> Unit,
    enabled: Boolean
) {
    TpSecondaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.sign_up),
        onClick = onClick,
        enabled = enabled
    )
}