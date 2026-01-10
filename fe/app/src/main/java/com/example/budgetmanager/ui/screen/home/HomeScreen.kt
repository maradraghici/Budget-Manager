package com.example.budgetmanager.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.R
import com.example.budgetmanager.ui.components.BudgetCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreenDestination(
    onBudgetClick: (Long) -> Unit,
    modifier: Modifier
) {
    val vm: HomeViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is HomeEffect.OnBudgetClick -> {
                    onBudgetClick(effect.id)
                }
            }
        }
    }

    HomeScreen(state, vm::onEvent, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(horizontal = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                Text(
                    text = "My budgets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 30.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 42.dp),
                    textAlign = TextAlign.Center
                )
            }

            items(state.budgetPreviewList) { budget ->
                BudgetCard(
                    title = budget.title,
                    description = budget.description,
                    image = painterResource(id = budgetImages[(budget.id % budgetImages.size).toInt()]),
                    onClick = {
                        onEvent(HomeEvent.OnBudgetClick(budget.id))
                    },
                    onHold = {
                        onEvent(HomeEvent.OnBudgetHold(budget.id, budget.owmerId))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }

        Button(
            onClick = { onEvent(HomeEvent.ShowCreateBudgetChanged) },
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
                text = "Add budget",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (state.showCreateBudget) {
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(HomeEvent.ShowCreateBudgetChanged)
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create a budget",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = state.budgetName,
                    onValueChange = {
                        if (it.length <= 30) {
                            onEvent(HomeEvent.BudgetNameChanged(it))
                        }
                    },
                    label = { Text("Name") },
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = state.budgetDescription,
                    onValueChange = {
                        if (it.length <= 100) {
                            onEvent(HomeEvent.BudgetDescriptionChanged(it))
                        }
                    },
                    label = { Text("Description") },
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onEvent(HomeEvent.ShowCreateBudgetChanged)
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
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }

                    Button(
                        onClick = {
                            onEvent(HomeEvent.CreateBudgetClicked)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onEvent(HomeEvent.ShowCreateBudgetChanged)
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
                            text = "Create",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    if (state.showDeleteBudget) {
        ModalBottomSheet(
            onDismissRequest = {
                onEvent(HomeEvent.ShowDeleteBudgetChanged)
            },
            sheetState = sheetState
        ) {
            Button(
                onClick = {
                    onEvent(HomeEvent.DeleteBudgetClicked)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onEvent(HomeEvent.ShowDeleteBudgetChanged)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Delete Budget",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

internal val budgetImages = listOf(
    R.drawable.budget0,
    R.drawable.budget1,
    R.drawable.budget2
)