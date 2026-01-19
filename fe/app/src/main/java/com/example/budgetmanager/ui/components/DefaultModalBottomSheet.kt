package com.example.budgetmanager.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultModalBottomSheet(
    onDismiss: () -> Unit,
    dismissLabel: String,
    onConfirm: () -> Unit,
    confirmLabel: String,
    title: String? = null,
    firstFieldLabel: String,
    firstFieldValue: String,
    firstFieldValueChange: (String) -> Unit,
    secondFieldLabel: String? = null,
    secondFieldValue: String? = null,
    secondFieldValueChange: (String) -> Unit = { },
    thirdFieldLabel: String? = null,
    thirdFieldValue: String? = null,
    thirdFieldValueChange: (String) -> Unit = { },
    firstFieldKeyboardType: KeyboardType = KeyboardOptions.Default.keyboardType,
    secondFieldKeyboardType: KeyboardType = KeyboardOptions.Default.keyboardType,
    thirdFieldKeyboardType: KeyboardType = KeyboardOptions.Default.keyboardType,
    singleField: Boolean = false,
    thirdField: Boolean = false,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = firstFieldValue,
                onValueChange = { firstFieldValueChange(it) },
                label = { Text(firstFieldLabel) },
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = firstFieldKeyboardType),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            if (!singleField && secondFieldLabel != null && secondFieldValue != null) {
                OutlinedTextField(
                    value = secondFieldValue,
                    onValueChange = { secondFieldValueChange(it) },
                    label = { Text(secondFieldLabel) },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = secondFieldKeyboardType),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }

            if (thirdField && thirdFieldLabel != null && thirdFieldValue != null) {
                OutlinedTextField(
                    value = thirdFieldValue,
                    onValueChange = { thirdFieldValueChange(it) },
                    label = { Text(thirdFieldLabel) },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = thirdFieldKeyboardType),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = dismissLabel,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }

                Button(
                    onClick = {
                        onConfirm()
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = confirmLabel,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}