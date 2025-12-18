package com.budget.app.services;

import com.budget.app.model.Budget;
import com.budget.app.model.User;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.BudgetVo;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public Budget getBudgetById(Long budgetId) {
        return budgetRepository.findByBudgetId(budgetId);
    }

    public Budget createNewBudget(BudgetVo budgetVo) {
        User user = null;
        if (budgetVo.getUserId() != null) {
            user = userRepository.findById(budgetVo.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found: " + budgetVo.getUserId()));
        }

        Budget budget = Budget.builder()
                .budgetName(budgetVo.getBudgetName())
                .commentary(budgetVo.getCommentary())
                .user(user)
                .build();


        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long budgetId) {
        if (budgetRepository.existsById(budgetId)) {
            budgetRepository.deleteById(budgetId);
        } else {
            throw new IllegalArgumentException("Budget not found: " + budgetId);
        }
    }
}
