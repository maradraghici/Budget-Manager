package com.example.budgetmanager.ui.screen.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.budgetmanager.R
import com.example.budgetmanager.ui.screen.main.MainEvent
import com.example.budgetmanager.ui.screen.splash.LoadingScreen

@Composable
fun ProfileScreenDestination(
    navigateToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: ProfileViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effect.collect {
            when(it) {
                ProfileEffect.OnSignOutClick -> {
                    navigateToAuth()
                }
                is ProfileEffect.OnProfileChanged -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (state.isLoading) {
        LoadingScreen(modifier)
    } else {
        ProfileScreen(state, vm::onEvent, modifier = modifier)
    }
}

@Composable
private fun ProfileScreen(
    state: ProfileState,
    onEvent: (event: ProfileEvent) -> Unit,
    modifier: Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onEvent(ProfileEvent.ProfileImageChanged(uri)) }
    )
    val scrollState = rememberScrollState()

    val minHeaderHeight = 0.dp
    val maxHeaderHeight = 150.dp
    val headerHeight = (maxHeaderHeight - (scrollState.value * 0.4f).dp).coerceIn(minHeaderHeight, maxHeaderHeight)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .background(MaterialTheme.colorScheme.primary)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (state.imageUri != null) {
                rememberAsyncImagePainter(model = state.imageUri)
            } else {
                painterResource(id = R.drawable.placeholder_profile_icon)
            },
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(top = 50.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary)
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        )

        Text(
            text = state.user.username,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(top = 10.dp)
        )

        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = { newUsername ->
                        val usernameRegex = Regex("^[A-Za-z0-9. ]*\$")
                        if (newUsername.length <= 20 && newUsername.matches(usernameRegex)) {
                            onEvent(ProfileEvent.UsernameChanged(newUsername))
                        }
                    },
                    label = { Text("Username") },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )

                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = {
                        val phoneRegex = Regex("^\\+\\d{0,11}$")
                        if (it.matches(phoneRegex)) {
                            onEvent(ProfileEvent.PhoneNumberChanged(it))
                        }
                    },
                    label = { Text("Phone Number") },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = if (state.phoneNumber.isEmpty()) false else state.phoneNumber.length < 12,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )

                OutlinedTextField(
                    value = state.oldPassword,
                    onValueChange = { oldPassword ->
                        val passwordRegex = Regex("^[A-Za-z0-9,.?!@#\$]*\$")
                        if (oldPassword.matches(passwordRegex)) {
                            onEvent(ProfileEvent.OldPasswordChanged(oldPassword))
                        }
                    },
                    label = { Text("Old Password") },
                    visualTransformation = if (state.oldPasswordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    trailingIcon = {
                        val icon = if (state.oldPasswordVisibility) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        }
                        IconButton(onClick = { onEvent(ProfileEvent.ToggleOldPasswordVisibility) }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    isError = state.oldPassword.length < 6 && state.oldPassword.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { newPassword ->
                        val passwordRegex = Regex("^[A-Za-z0-9,.?!@#\$]*\$")
                        if (newPassword.matches(passwordRegex)) {
                            onEvent(ProfileEvent.PasswordChanged(newPassword))
                        }
                    },
                    label = { Text("New Password") },
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
                        IconButton(onClick = { onEvent(ProfileEvent.TogglePasswordVisibility) }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    isError = state.password.length < 6 && state.password.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )

                OutlinedTextField(
                    value = state.secondPassword,
                    onValueChange = { newPassword ->
                        val passwordRegex = Regex("^[A-Za-z0-9,.?!@#\$]*\$")
                        if (newPassword.matches(passwordRegex)) {
                            onEvent(ProfileEvent.SecondPasswordChanged(newPassword))
                        }
                    },
                    label = { Text("Confirm New Password") },
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
                        IconButton(onClick = { onEvent(ProfileEvent.ToggleSecondPasswordVisibility) }) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Toggle second password visibility"
                            )
                        }
                    },
                    isError = state.secondPassword.isNotEmpty() && state.secondPassword.isNotEmpty() &&
                            state.secondPassword != state.password,
                    supportingText = {
                        if (state.secondPassword.isNotEmpty() && state.password.isNotEmpty() &&
                            state.secondPassword != state.password) {
                            Text(
                                text = "Passwords do not match",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onEvent(ProfileEvent.OnConfirmClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    enabled = state.phoneNumber.length == 12 && state.username.isNotBlank() && (
                            (state.oldPassword.length >= 6 && state.password.length >= 6 &&
                            state.secondPassword == state.password) ||
                            (state.password.isEmpty()) && (state.username != state.user.username ||
                            state.phoneNumber != state.user.phoneNumber))
                ) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Button(
                    onClick = { onEvent(ProfileEvent.OnSignOutClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.inversePrimary
                    )
                ) {
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}