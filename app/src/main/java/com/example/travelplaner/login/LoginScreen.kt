package com.example.travelplaner.login

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelplaner.R
import com.example.travelplaner.core.util.collectAsStateWithLifecycle
import com.example.travelplaner.destinations.HomeScreenDestination
import com.example.travelplaner.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(
    start = true
)
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle(initial = LoginViewState.Initial)

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

    val context = LocalContext.current
    LoginScreenContent(
        viewState = viewState,
        onUsernameChanged = viewModel.onUsernameChanged,
        onPasswordChanged = viewModel.onPasswordChanged,
        onLoginClicked = viewModel.onLoginClicked,
        onSignUpClicked = {
            Toast.makeText(
                context,
                context.getString(R.string.not_supported),
                Toast.LENGTH_SHORT
            ).show()
        }
    )
}