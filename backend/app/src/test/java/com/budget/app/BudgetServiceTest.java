package com.budget.app;

import com.budget.app.model.Budget;
import com.budget.app.model.Expends;
import com.budget.app.model.User;
import com.budget.app.model.UserBudget;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserBudgetRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.services.BudgetService;
import com.budget.app.vo.BudgetResponseVo;
import com.budget.app.vo.BudgetVo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserBudgetRepository userBudgetRepository;
    @Mock
    private ExpendsRepository expendsRepository;

    @InjectMocks
    private BudgetService budgetService;


    @Test
    void testCreateNewBudget_Success() {
        BudgetVo vo = new BudgetVo();
        vo.setBudgetName("Summer 2024");
        vo.setCommentary("Trip to Vama Veche");
        vo.setUserId(1L);
        User mockUser = new User();
        mockUser.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(i -> i.getArguments()[0]);

        Budget result = budgetService.createNewBudget(vo);

        assertNotNull(result);
        assertEquals("Summer 2024", result.getBudgetName());
        assertEquals(mockUser, result.getUser());

        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void testGetBudgetById_Success() {

        Long budgetId = 100L;
        User owner = new User(); owner.setUserId(1L); owner.setUserName("Mara");

        Budget budget = Budget.builder()
                .budgetId(budgetId)
                .budgetName("Project Budget")
                .user(owner)
                .build();

        when(budgetRepository.findByBudgetId(budgetId)).thenReturn(budget);

        BudgetResponseVo result = budgetService.getBudgetById(budgetId);

        assertEquals("Project Budget", result.getBudgetName());
        assertEquals("Mara", result.getUser().getUserName());
    }

    @Test
    void testGetBudgetByCreator_ReturnsList() {
        Long userId = 1L;
        User owner = new User(); owner.setUserId(userId);
        Budget b1 = Budget.builder().budgetName("B1").user(owner).build();
        Budget b2 = Budget.builder().budgetName("B2").user(owner).build();

        when(budgetRepository.findByUser_UserId(userId)).thenReturn(List.of(b1, b2));

        List<BudgetResponseVo> results = budgetService.getBudgetByCreator(userId);

        assertEquals(2, results.size());
    }

    @Test
    void testDeleteBudget_Success_CascadingDelete() {
        Long budgetId = 50L;

        List<UserBudget> linkedUsers = List.of(new UserBudget());
        List<Expends> linkedExpends = List.of(new Expends());

        when(userBudgetRepository.findByBudgetId_BudgetId(budgetId)).thenReturn(linkedUsers);
        when(expendsRepository.findByBudget_BudgetId(budgetId)).thenReturn(linkedExpends);
        when(budgetRepository.existsById(budgetId)).thenReturn(true);

        budgetService.deleteBudget(budgetId);

        verify(userBudgetRepository).deleteAll(linkedUsers); // Must delete participants first
        verify(expendsRepository).deleteAll(linkedExpends);  // Must delete expenses first
        verify(budgetRepository).deleteById(budgetId);       // Finally delete budget
    }

    @Test
    void testDeleteBudgetByUserId_Success() {
        Long userId = 1L;
        List<UserBudget> userBudgets = List.of(new UserBudget());
        List<Budget> budgets = List.of(new Budget());

        when(userBudgetRepository.findByUserId_UserId(userId)).thenReturn(userBudgets);
        when(budgetRepository.findByUser_UserId(userId)).thenReturn(budgets);

        budgetService.deleteBudgetByUserId(userId);

        verify(userBudgetRepository).deleteAll(userBudgets);
        verify(budgetRepository).deleteAll(budgets);
    }


    @Test
    void testCreateNewBudget_FailsWhenUserNotFound() {
        BudgetVo vo = new BudgetVo();
        vo.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createNewBudget(vo));

        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void testCreateNewBudget_FailsWhenNameIsBlank() {
        BudgetVo vo = new BudgetVo();
        vo.setUserId(1L);
        vo.setBudgetName(""); // Invalid

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> budgetService.createNewBudget(vo));

        assertEquals("Budget name is required", ex.getMessage());
    }

    @Test
    void testGetBudgetById_NotFound() {
        Long budgetId = 100L;
        when(budgetRepository.findByBudgetId(budgetId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> budgetService.getBudgetById(budgetId));

        assertEquals("Budget not found: 100", ex.getMessage());
    }

    @Test
    void testDeleteBudget_NotFound() {
        Long budgetId = 50L;

        // even if dependencies are empty
        when(userBudgetRepository.findByBudgetId_BudgetId(budgetId)).thenReturn(Collections.emptyList());
        when(expendsRepository.findByBudget_BudgetId(budgetId)).thenReturn(Collections.emptyList());
        // if the budget  doesn't exist:
        when(budgetRepository.existsById(budgetId)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> budgetService.deleteBudget(budgetId));

        assertEquals("Budget not found: 50", ex.getMessage());
        verify(budgetRepository, never()).deleteById(any());
    }
}