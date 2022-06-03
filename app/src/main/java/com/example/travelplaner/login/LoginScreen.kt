package com.example.travelplaner.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.core.ui.theme.TextFieldShape
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.HomeScreenDestination
import com.example.travelplaner.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination()
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initial = LoginViewState.Initial)

    var showSignUpDialog by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(
        key1 = viewState,
        block = {
            if (viewState is LoginViewState.Completed) {
                navigator.navigate(HomeScreenDestination.route) {
                    this.popUpTo(LoginScreenDestination.route) {
                        this.inclusive = true
                    }
                }
            }
        }
    )

    LoginScreenContent(
        viewState = viewState,
        onUsernameChanged = viewModel.onUsernameChanged,
        onPasswordChanged = viewModel.onPasswordChanged,
        onLoginClicked = viewModel.onLoginClicked,
        onSignUpClicked = { showSignUpDialog = true }
    )

    if (showSignUpDialog) {

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = { showSignUpDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .clip(TextFieldShape)
                    .background(MaterialTheme.colors.surface)
                    .padding(24.dp),
            ) {
                Text(
                    text = "Create a new account",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
                )
                Spacer(modifier = Modifier.height(8.dp))
                UsernameInput(
                    text = username,
                    onTextChanged = { username = it },
                    errorMessage = null,
                    enabled = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordInput(
                    text = password,
                    onTextChanged = { password = it },
                    errorMessage = null,
                    enabled = true,
                    focusManager = focusManager
                )
                Spacer(modifier = Modifier.height(16.dp))
                SignUpButton(
                    onClick = {
                        viewModel.onSignUp(username, password)
                        showSignUpDialog = false
                    },
                    enabled = true
                )
            }
        }
    }
}