package com.example.budgetmanager.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgetmanager.ui.components.TextButton

@Composable
fun SettingsScreenDestination(
    onConnectionsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: SettingsViewModel = viewModel()

    LaunchedEffect(Unit) {
        vm.effect.collect {
            when (it) {
                SettingsEffect.OnConnectionsClick -> onConnectionsClick()
                SettingsEffect.OnProfileClick -> onProfileClick()
            }
        }
    }

    SettingsScreen(vm::onEvent, modifier = modifier)
}

@Composable
private fun SettingsScreen(
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
    Column(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .height(80.dp)
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
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 30.sp
                ),
                modifier = Modifier
                    .padding(vertical = 60.dp)
            )

            TextButton(
                text = "My Profile",
                onClick = { onEvent(SettingsEvent.OnProfileClick) }
            )

            TextButton(
                text = "My Connections",
                onClick = { onEvent(SettingsEvent.OnConnectionsClick) }
            )
        }
    }
}