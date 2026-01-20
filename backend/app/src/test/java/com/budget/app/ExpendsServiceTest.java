package com.budget.app.services;

import com.budget.app.model.Budget;
import com.budget.app.model.Expends;
import com.budget.app.model.User;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.ExpendsResponseVo;
import com.budget.app.vo.ExpendsVo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpendsServiceTest {

    @Mock private ExpendsRepository expendsRepository;
    @Mock private UserRepository userRepository;
    @Mock private BudgetRepository budgetRepository;

    @InjectMocks private ExpendsService expendsService;

    @Test
    void testCreateNewExpends_Success() {
        // 1. Setup Input VO
        ExpendsVo vo = new ExpendsVo();
        vo.setExpendsName("Groceries");
        vo.setAmount(50.0);
        vo.setBudgetId(1L);
        vo.setPaidById(10L);

        User mockUser = new User();
        mockUser.setUserId(10L);
        mockUser.setUserName("Mara");

        Budget mockBudget = new Budget();
        mockBudget.setBudgetId(1L);
        mockBudget.setBudgetName("Home");

        when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(mockBudget));

        when(expendsRepository.save(any(Expends.class))).thenAnswer(invocation -> {
            Expends saved = invocation.getArgument(0);
            saved.setExpendsId(100L);
            return saved;
        });

        Expends result = expendsService.createNewExpends(vo);

        assertNotNull(result);
        assertEquals(100L, result.getExpendsId());
        assertEquals(50.0, result.getAmount());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockBudget, result.getBudget());
    }

    @Test
    void testGetExpendsByBudgetId_MappingLogic() {
        User payer = new User();
        payer.setUserId(5L);
        payer.setUserName("Radu");
        payer.setEmail("radu@test.com");

        Budget budget = new Budget();
        budget.setBudgetId(2L);
        budget.setBudgetName("Trip");

        Expends expense = Expends.builder()
                .expendsId(10L)
                .expendsName("Taxi")
                .amount(25.0)
                .user(payer)
                .budget(budget)
                .build();

        when(expendsRepository.findByBudget_BudgetId(2L)).thenReturn(List.of(expense));

        List<ExpendsResponseVo> result = expendsService.getExpendsByBudgetId(2L);

        assertEquals(1, result.size());
        assertEquals("Radu", result.get(0).getPaidBy().getUserName());
        assertEquals("Trip", result.get(0).getBudget().getBudgetName());
    }
}