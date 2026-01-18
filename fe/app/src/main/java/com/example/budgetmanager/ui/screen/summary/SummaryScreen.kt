package com.example.budgetmanager.ui.screen.summary

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.ui.screen.splash.LoadingScreen

@Composable
fun SummaryScreenDestination(
    onTitleChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vm: SummaryViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        onTitleChanged("Summary")
    }

    if (state.isLoading) {
        LoadingScreen(modifier)
    } else {
        SummaryScreen(state, modifier)
    }
}

@Composable
fun SummaryScreen(state: SummaryState, modifier: Modifier) {
    LazyColumn(modifier = modifier.padding(horizontal = 24.dp)) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Amount paid",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        items(state.totalPerUserList) { user ->
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${user.amount}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(42.dp))

            Box (modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${state.total}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        ),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 60.dp),
                color = MaterialTheme.colorScheme.secondary,
                thickness = 2.dp
            )
        }

        items(state.debts) { userDebt ->
            Column{
                Text(
                    text = "${userDebt.from} owes ${userDebt.amount} to ${userDebt.to}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}