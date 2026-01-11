package com.example.budgetmanager.ui.screen.connections

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.ui.components.ConfirmModalBottomSheet
import com.example.budgetmanager.ui.components.DefaultModalBottomSheet
import com.example.budgetmanager.ui.components.TextButton

@Composable
fun ConnectionsScreenDestination(
    onTitleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: ConnectionsViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        onTitleChanged("My Connections")
    }

    ConnectionsScreen(state, vm::onEvent, modifier = modifier)
}

@Composable
private fun ConnectionsScreen(
    state: ConnectionsState,
    onEvent: (ConnectionsEvent) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(state.connectionsList) { connection ->
                TextButton(
                    text = connection.username,
                    hasTrailingButton = false,
                    onClick = { onEvent(ConnectionsEvent.OnConnectionClick(connection.id)) }
                )
            }
        }
        Button(
            onClick = { onEvent(ConnectionsEvent.ShowAddConnectionChanged) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 30.dp, vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Add Connection",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (state.showDeleteConnection) {
        ConfirmModalBottomSheet(
            label = "Delete",
            onDismiss = { onEvent(ConnectionsEvent.ShowDeleteConnectionChanged) },
            onConfirm = { onEvent(ConnectionsEvent.OnDeleteConnectionClicked) }
        )
    }

    if (state.showAddConnection) {
        DefaultModalBottomSheet(
            onDismiss = { onEvent(ConnectionsEvent.ShowAddConnectionChanged) },
            dismissLabel = "Cancel",
            onConfirm = {
                if (state.addConnectionPhoneNumber.length == 12 && state.addConnectionPhoneNumber.startsWith("+")) {
                    onEvent(ConnectionsEvent.OnAddConnectionClicked)
                } else {
                    Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
                }
            },
            confirmLabel = "Add",
            title = "Add Connection",
            firstFieldLabel = "Phone Number",
            firstFieldValue = state.addConnectionPhoneNumber,
            firstFieldValueChange = {
                val phoneRegex = Regex("^\\+\\d{0,11}$")
                if (it.matches(phoneRegex)) {
                    onEvent(ConnectionsEvent.AddConnectionPhoneNumberChanged(it))
                }
            },
            firstFieldKeyboardType = KeyboardType.Phone
        )
    }
}