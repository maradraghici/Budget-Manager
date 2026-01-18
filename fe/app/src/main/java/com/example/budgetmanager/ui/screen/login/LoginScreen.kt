package com.example.budgetmanager.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.R
import com.example.budgetmanager.ui.screen.signup.SignUpEvent
import com.example.budgetmanager.ui.screen.splash.LoadingScreen

@Composable
fun LoginScreenDestination(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm : LoginViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                LoginEffect.OnLoginSuccess -> onLoginSuccess()
                LoginEffect.OnSignUpClick -> onSignUpClick()
                is LoginEffect.OnError -> {
                    Toast.makeText(context, effect.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingScreen(modifier)
    } else {
        LoginScreen(state, vm::onEvent, modifier)
    }
}


@Composable
fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.primary)
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()).weight(1f)
        ) {
            Spacer(
                modifier = Modifier
                    .height(53.dp)
                    .fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp
                            )
                        )
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = "Welcome back",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Hello there, sign in to continue",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .width(180.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    OutlinedTextField(
                        value = state.username,
                        onValueChange = { newUsername ->
                            val usernameRegex = Regex("^[A-Za-z0-9. ]*\$")
                            if (newUsername.length <= 20 && newUsername.matches(usernameRegex)) {
                                onEvent(LoginEvent.UsernameChanged(newUsername))
                            }
                        },
                        label = { Text("Username") },
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { newPassword ->
                            val passwordRegex = Regex("^[A-Za-z0-9,.?!@#\$]*\$")
                            if (newPassword.matches(passwordRegex)) {
                                onEvent(LoginEvent.PasswordChanged(newPassword))
                            }
                        },
                        label = { Text("Password") },
                        visualTransformation = if (state.passwordVisibility) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        trailingIcon = {
                            val icon = if (state.passwordVisibility) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            }
                            IconButton(onClick = { onEvent(LoginEvent.TogglePasswordVisibility) }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        isError = state.password.length < 6 && state.password.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(54.dp))

                    Button(
                        onClick = { onEvent(LoginEvent.LoginClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        enabled = state.username.isNotBlank() && state.password.length >= 6

                    ) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onEvent(LoginEvent.OnSignUpClick) }
            )
        }
    }
}