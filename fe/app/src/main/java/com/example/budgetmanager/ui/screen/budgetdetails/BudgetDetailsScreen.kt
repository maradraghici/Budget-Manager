package com.example.budgetmanager.ui.screen.budgetdetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.ui.components.ExpenseCard
import com.example.budgetmanager.ui.screen.main.TopBarEvent

@Composable
fun BudgetDetailsScreenDestination(
    modifier: Modifier = Modifier,
    onTopBarEvent: (event: TopBarEvent) -> Unit
) {
    val vm: BudgetDetailsViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(state.budgetDetails?.title) {
        state.budgetDetails?.let {
            onTopBarEvent(TopBarEvent.TitleChanged(it.title))
        }
    }

    BudgetDetailsScreen(state, vm::onEvent, modifier)
}

@Composable
private fun BudgetDetailsScreen(
    state: BudgetDetailsState,
    onEvent: (BudgetDetailsEvent) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier.padding(horizontal = 24.dp).padding(top = 24.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            state.budgetDetails?.let {
                items(it.expenses) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onClick = { },
                        onHold = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text(
                    text = "View Summary",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}