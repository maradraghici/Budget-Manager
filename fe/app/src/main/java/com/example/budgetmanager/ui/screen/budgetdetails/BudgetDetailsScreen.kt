package com.example.budgetmanager.ui.screen.budgetdetails

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.budgetmanager.ui.components.ConfirmModalBottomSheet
import com.example.budgetmanager.ui.components.DefaultModalBottomSheet
import com.example.budgetmanager.ui.components.ExpenseCard
import com.example.budgetmanager.ui.components.OptionsModalBottomSheet
import com.example.budgetmanager.ui.screen.main.MainEvent

@Composable
fun BudgetDetailsScreenDestination(
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit,
    navigateToSummary: (Long) -> Unit
) {
    val vm: BudgetDetailsViewModel = hiltViewModel()
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is BudgetDetailsEffect.OnSummaryClick -> navigateToSummary(effect.id)
            }
        }
    }

    LaunchedEffect(state.budgetDetails?.title) {
        state.budgetDetails?.let {
            onTitleChanged(it.title)
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
    val context = LocalContext.current

    Column(modifier = modifier.padding(horizontal = 24.dp).padding(top = 24.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            state.budgetDetails?.let {
                items(it.expenses) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onClick = { onEvent(BudgetDetailsEvent.OnExpenseClicked(expense.id, expense.user.id)) },
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
                onClick = { onEvent(BudgetDetailsEvent.OptionsClicked) },
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
                onClick = { onEvent(BudgetDetailsEvent.OnSummaryClick) },
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

    if (state.showDeleteExpense) {
        ConfirmModalBottomSheet(
            onDismiss = { onEvent(BudgetDetailsEvent.ShowDeleteExpenseChanged) },
            onConfirm = { onEvent(BudgetDetailsEvent.DeleteExpenseClicked) },
            label = "Delete Expense"
        )
    }

    if (state.showOptions) {
        OptionsModalBottomSheet(
            onDismiss = { onEvent(BudgetDetailsEvent.OptionsClicked) },
            text1 = "Add Expense",
            text2 = "Add User",
            text3 = "Remove User",
            onClick1 = { onEvent(BudgetDetailsEvent.ShowAddExpenseChanged) },
            onClick2 = { onEvent(BudgetDetailsEvent.ShowAddUserChanged) },
            onClick3 = { onEvent(BudgetDetailsEvent.ShowRemoveUserChanged) }
        )
    }

    if (state.showAddExpense) {
        DefaultModalBottomSheet(
            onDismiss = { onEvent(BudgetDetailsEvent.ShowAddExpenseChanged) },
            dismissLabel = "Cancel",
            onConfirm = {
                if (state.newExpenseName.isEmpty() || state.newExpensePrice.isEmpty()) {
                    Toast.makeText(context, "Invalid fields", Toast.LENGTH_SHORT).show()
                } else {
                    onEvent(BudgetDetailsEvent.AddExpenseClicked)
                }
            },
            confirmLabel = "Add",
            title = "Add Expense",
            firstFieldLabel = "Name",
            firstFieldValue = state.newExpenseName,
            firstFieldValueChange = {
                if (it.length < 30) {
                    onEvent(BudgetDetailsEvent.NewExpenseNameChanged(it))
                }
            },
            secondFieldLabel = "Price",
            secondFieldValue = state.newExpensePrice,
            secondFieldValueChange = {
                val priceRegex = Regex("^[0-9]+(\\.[0-9]{0,2})?$")
                if (it.isEmpty() || it.matches(priceRegex)) {
                    onEvent(BudgetDetailsEvent.NewExpensePriceChanged(it))
                }
            },
            secondFieldKeyboardType = KeyboardType.Decimal
        )
    }

    if (state.showAddUser) {
        DefaultModalBottomSheet(
            onDismiss = { onEvent(BudgetDetailsEvent.ShowAddUserChanged) },
            dismissLabel = "Cancel",
            onConfirm = {
                if (state.newUserPhoneNumber.length == 12 && state.newUserPhoneNumber.startsWith("+")) {
                    onEvent(BudgetDetailsEvent.AddUserClicked)
                } else {
                    Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
                }
            },
            confirmLabel = "Add",
            title = "Add User",
            firstFieldLabel = "Phone Number",
            firstFieldValue = state.newUserPhoneNumber,
            firstFieldValueChange = {
                val phoneRegex = Regex("^\\+\\d{0,11}$")
                if (it.matches(phoneRegex)) {
                    onEvent(BudgetDetailsEvent.NewUserPhoneNumberChanged(it))
                }
            },
            firstFieldKeyboardType = KeyboardType.Phone
        )
    }

    if (state.showRemoveUser) {
        DefaultModalBottomSheet(
            onDismiss = { onEvent(BudgetDetailsEvent.ShowRemoveUserChanged) },
            dismissLabel = "Cancel",
            onConfirm = {
                if (state.removeUserPhoneNumber.length == 12 && state.removeUserPhoneNumber.startsWith("+")) {
                    onEvent(BudgetDetailsEvent.RemoveUserClicked)
                } else {
                    Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
                }
            },
            confirmLabel = "Remove",
            title = "Remove User",
            firstFieldLabel = "Phone Number",
            firstFieldValue = state.removeUserPhoneNumber,
            firstFieldValueChange = {
                val phoneRegex = Regex("^\\+\\d{0,11}$")
                if (it.matches(phoneRegex)) {
                    onEvent(BudgetDetailsEvent.RemoveUserPhoneNumberChanged(it))
                }
            },
            firstFieldKeyboardType = KeyboardType.Phone
        )
    }
}