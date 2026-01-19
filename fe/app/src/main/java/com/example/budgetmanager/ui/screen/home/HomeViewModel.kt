package com.example.budgetmanager.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetmanager.data.local.Budget
import com.example.budgetmanager.data.local.UserPreferences
import com.example.budgetmanager.data.remote.dto.CreateBudgetRequest
import com.example.budgetmanager.data.repository.BudgetsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val budgetPreviewList: List<Budget> = emptyList(),
    val budgetName: String = "",
    val budgetDescription: String = "",
    val deleteBudgetId: Long? = null,
    val showCreateBudget: Boolean = false,
    val showDeleteBudget: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface HomeEvent {
    data class BudgetNameChanged(val name: String) : HomeEvent
    data class BudgetDescriptionChanged(val description: String) : HomeEvent
    data object ShowCreateBudgetChanged : HomeEvent
    data object ShowDeleteBudgetChanged : HomeEvent
    data object CreateBudgetClicked : HomeEvent
    data object DeleteBudgetClicked : HomeEvent
    data class OnBudgetHold(val id: Long) : HomeEvent

    data class OnBudgetClick(val id: Long, val title: String) : HomeEvent
}

sealed interface HomeEffect {
    data class OnBudgetClick(val id: Long, val title: String) : HomeEffect
    data class OnBudgetsChanged(val meessage: String) : HomeEffect
}


@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val budgetsRepository: BudgetsRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ShowCreateBudgetChanged -> {
                if (_state.value.showCreateBudget) {
                    _state.value = _state.value.copy(
                        showCreateBudget = false,
                        budgetName = "",
                        budgetDescription = ""
                    )
                } else {
                    _state.value = _state.value.copy(
                        showCreateBudget = true
                    )
                }
            }
            is HomeEvent.ShowDeleteBudgetChanged -> {
                _state.value = _state.value.copy(
                    showDeleteBudget = false,
                    deleteBudgetId = null
                )
            }
            is HomeEvent.BudgetDescriptionChanged -> {
                _state.value = _state.value.copy(budgetDescription = event.description)
            }
            is HomeEvent.BudgetNameChanged -> {
                _state.value = _state.value.copy(budgetName = event.name)
            }
            is HomeEvent.CreateBudgetClicked -> {
                createBudget()
            }
            is HomeEvent.DeleteBudgetClicked -> {
                deleteBudget()
            }
            is HomeEvent.OnBudgetHold -> {
                val budget = _state.value.budgetPreviewList.find { it.id == event.id }
                if(budget?.isOwner == true) {
                    _state.value = _state.value.copy(
                        showDeleteBudget = true,
                        deleteBudgetId = event.id
                    )
                }
            }

            is HomeEvent.OnBudgetClick -> viewModelScope.launch {
                _effect.emit(HomeEffect.OnBudgetClick(event.id, event.title))
            }
        }
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val storedUserId = UserPreferences.userIdFlow(context).first()
            storedUserId?.let {
                val responseOwner = budgetsRepository.getBudgetsForOwner(storedUserId)
                if (responseOwner.isSuccessful && responseOwner.body() != null) {
                    _state.value = _state.value.copy(budgetPreviewList = responseOwner.body()!!)
                }

                val responseMember = budgetsRepository.getBudgetsForMember(storedUserId)
                if (responseMember.isSuccessful && responseMember.body() != null) {
                    _state.value = _state.value.copy(
                        budgetPreviewList = _state.value.budgetPreviewList + responseMember.body()!!
                    )
                }

            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun createBudget() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val storedUserId = UserPreferences.userIdFlow(context).first()

            storedUserId?.let {
                val request = CreateBudgetRequest(
                    budgetName = _state.value.budgetName,
                    budgetDescription = _state.value.budgetDescription,
                    userId = storedUserId
                )
                val response = budgetsRepository.createBudget(request)
                if (response.isSuccessful) {
                    _effect.emit(HomeEffect.OnBudgetsChanged("Budget created successfully"))
                    loadBudgets()
                } else {
                    _effect.emit(HomeEffect.OnBudgetsChanged("Failed to create budget"))
                }
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun deleteBudget() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            _state.value.deleteBudgetId?.let {
                val response = budgetsRepository.deleteBudget(it)
                if (response.isSuccessful) {
                    _effect.emit(HomeEffect.OnBudgetsChanged("Budget deleted successfully"))
                    loadBudgets()
                } else {
                    _effect.emit(HomeEffect.OnBudgetsChanged("Failed to delete budget"))
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    init {
        loadBudgets()
    }

}