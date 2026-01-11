package com.example.budgetmanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    hasTrailingButton: Boolean = true
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.weight(1f)
        )
        if (hasTrailingButton) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.tertiary
    )
}