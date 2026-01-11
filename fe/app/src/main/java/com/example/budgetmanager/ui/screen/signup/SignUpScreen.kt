package com.example.budgetmanager.ui.screen.signup

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetmanager.R
import com.example.budgetmanager.ui.theme.BudgetManagerTheme

@Composable
fun SignUpScreenDestination(
    onSignUpSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm : SignUpViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                SignUpEffect.OnSignUpSuccess -> onSignUpSuccess()
                SignUpEffect.OnLoginClick -> onLoginClick()
            }
        }
    }

    SignUpScreen(state, vm::onEvent, modifier)
}


@Composable
private fun SignUpScreen(
    state: SignUpState,
    onEvent: (SignUpEvent) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.primary)
    )

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column(
            modifier = modifier.verticalScroll(rememberScrollState()).weight(1f)
        ) {
            Spacer(
                modifier = Modifier
                    .height(53.dp)
                    .fillMaxWidth()
            )

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
                    text = "Welcome to us,",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hello there, create a New Account",
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
                            onEvent(SignUpEvent.UsernameChanged(newUsername))
                        }
                    },
                    label = { Text("Username") },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                var phoneFieldValue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = state.phoneNumber,
                            selection = TextRange(state.phoneNumber.length)
                        )
                    )
                }

                OutlinedTextField(
                    value = phoneFieldValue,
                    onValueChange = { newValue ->
                        var phone = newValue.text

                        if (phone.isNotEmpty() && !phone.startsWith("+")) {
                            phone = "+$phone"
                        }

                        val phoneRegex = Regex("^\\+?\\d{0,11}$")
                        if (!phone.matches(phoneRegex)) return@OutlinedTextField

                        phoneFieldValue = TextFieldValue(
                            text = phone,
                            selection = TextRange(phone.length)
                        )

                        onEvent(SignUpEvent.PhoneNumberChanged(phone))
                    },
                    label = { Text("Phone Number") },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = if (state.phoneNumber.isEmpty()) false else state.phoneNumber.length < 12,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { newPassword ->
                        val passwordRegex = Regex("^[A-Za-z0-9,.?!@#\$]*\$")
                        if (newPassword.matches(passwordRegex)) {
                            onEvent(SignUpEvent.PasswordChanged(newPassword))
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
                        IconButton(onClick = { onEvent(SignUpEvent.TogglePasswordVisibility) }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    isError = state.password.length < 6 && state.password.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    value = state.secondPassword,
                    onValueChange = { newPassword ->
                        val passwordRegex = Regex("^[A-Za-z0-9,.?!@#\$]*\$")
                        if (newPassword.matches(passwordRegex)) {
                            onEvent(SignUpEvent.SecondPasswordChanged(newPassword))
                        }
                    },
                    label = { Text("Confirm Password") },
                    visualTransformation = if (state.secondPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    trailingIcon = {
                        val icon = if (state.secondPasswordVisibility) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        }
                        IconButton(onClick = { onEvent(SignUpEvent.ToggleSecondPasswordVisibility) }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle second password visibility"
                            )
                        }
                    },
                    isError = state.secondPassword.isNotEmpty() && state.secondPassword.isNotEmpty() &&
                            state.secondPassword != state.password,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = state.termsAccepted,
                        onCheckedChange = { onEvent(SignUpEvent.TermsAcceptedChanged) }
                    )

                    val uriHandler = LocalUriHandler.current

                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                            append("By creating an account you agree to our ")
                        }
                        // To be changed to the actual link
                        pushStringAnnotation(
                            tag = "TERMS",
                            annotation = "https://youtu.be/dQw4w9WgXcQ?si=Y0SKQcGW2wXSzNrq"
                        )
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("Terms and Conditions")
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.labelSmall,
                        onClick = { offset: Int ->
                            annotatedString.getStringAnnotations(
                                tag = "TERMS",
                                start = offset,
                                end = offset
                            ).firstOrNull()?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                        },
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    onClick = { onEvent(SignUpEvent.SignUpClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = state.phoneNumber.length == 12 && state.password.length >= 6 &&
                            state.secondPassword == state.password && state.termsAccepted && state.username.isNotBlank()
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onEvent(SignUpEvent.OnLoginClick) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    BudgetManagerTheme {
        SignUpScreenDestination(onSignUpSuccess = {}, onLoginClick = {})
    }
}