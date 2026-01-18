package com.example.budgetmanager.ui.screen.home

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.R
import com.example.budgetmanager.ui.components.BudgetCard
import com.example.budgetmanager.ui.components.ConfirmModalBottomSheet
import com.example.budgetmanager.ui.components.DefaultModalBottomSheet
import com.example.budgetmanager.ui.screen.splash.LoadingScreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreenDestination(
    onBudgetClick: (Long, String) -> Unit,
    modifier: Modifier
) {
    val vm: HomeViewModel = hiltViewModel()
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is HomeEffect.OnBudgetClick -> {
                    onBudgetClick(effect.id, effect.title)
                }
                is HomeEffect.OnBudgetsChanged -> {
                    Toast.makeText(context, effect.meessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    HomeScreen(state, vm::onEvent, modifier = modifier)
}

@Composable
private fun HomeScreen(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier
) {
    if (state.isLoading) {
        LoadingScreen(modifier)
    }

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
                        onEvent(HomeEvent.OnBudgetClick(budget.id, budget.title))
                    },
                    onHold = {
                        onEvent(HomeEvent.OnBudgetHold(budget.id))
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
        DefaultModalBottomSheet(
            title = "Create a budget",
            onDismiss = { onEvent(HomeEvent.ShowCreateBudgetChanged) },
            onConfirm = { onEvent(HomeEvent.CreateBudgetClicked) },
            dismissLabel = "Cancel",
            confirmLabel = "Create",
            firstFieldLabel = "Name",
            firstFieldValue = state.budgetName,
            firstFieldValueChange = {
                if (it.length <= 30) {
                    onEvent(HomeEvent.BudgetNameChanged(it))
                }
            },
            secondFieldLabel = "Description",
            secondFieldValue = state.budgetDescription,
            secondFieldValueChange = {
                if (it.length <= 100) {
                    onEvent(HomeEvent.BudgetDescriptionChanged(it))
                }
            }
        )
    }

    if (state.showDeleteBudget) {
        ConfirmModalBottomSheet(
            onDismiss = { onEvent(HomeEvent.ShowDeleteBudgetChanged) },
            onConfirm = { onEvent(HomeEvent.DeleteBudgetClicked) },
            label = "Delete Budget"
        )
    }
}

internal val budgetImages = listOf(
    R.drawable.budget0,
    R.drawable.budget1,
    R.drawable.budget2
)