package com.budget.app.services;

import com.budget.app.model.UserBudget;
import com.budget.app.model.Budget;
import com.budget.app.model.User;
import com.budget.app.repository.UserBudgetRepository;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.UserBudgetVo;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserBudgetService {
    private final UserBudgetRepository userBudgetRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public UserBudgetService(UserBudgetRepository userBudgetRepository, BudgetRepository budgetRepository, UserRepository userRepository) {
        this.userBudgetRepository = userBudgetRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public List<UserBudget> getUserBudgetByUserId(Long userId) {
        return userBudgetRepository.findByUserId_UserId(userId);
    }

    public UserBudget getUserBudgetById(Long userBudgetId) {
        return userBudgetRepository.findByUserBudgetId(userBudgetId);
    }

    public List<UserBudget> getUserBudgetByBudgetId(Long budgetId) {
        return userBudgetRepository.findByBudgetId_BudgetId(budgetId);
    }

    public UserBudget createUserBudget(UserBudgetVo userBudget) {
        User user = null;
        if (userBudget.getUserId() != null) {
            user = userRepository.findById(userBudget.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found: " + userBudget.getUserId()));
        }
        Budget budget = null;
        if (userBudget.getBudgetId() != null) {
            budget = budgetRepository.findById(userBudget.getBudgetId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Budget not found: " + userBudget.getBudgetId()));
        }

        UserBudget newUserBudget = UserBudget.builder()
                .userId(user) // Set userId appropriately
                .budgetId(budget) // Set budgetId appropriately
                .build();

        return userBudgetRepository.save(newUserBudget);
    }

    public void deleteUserBudget(Long userBudgetId) {
        userBudgetRepository.deleteById(userBudgetId);
    }
}
