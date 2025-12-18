package com.budget.app.services;

import com.budget.app.model.Expends;
import com.budget.app.model.User;
import com.budget.app.model.Budget;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.vo.ExpendsVo;

import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpendsService {
    private final ExpendsRepository expendsRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public ExpendsService(ExpendsRepository expendsRepository, UserRepository userRepository, BudgetRepository budgetRepository) {
        this.expendsRepository = expendsRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    public Expends createNewExpends(ExpendsVo expendsVo) {
        User user = null;
        if (expendsVo.getPaidById() != null) {
            user = userRepository.findById(expendsVo.getPaidById())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found: " + expendsVo.getPaidById()));
        }

        Budget budget = null;
        if (expendsVo.getBudgetId() != null) {
            budget = budgetRepository.findById(expendsVo.getBudgetId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Budget not found: " + expendsVo.getBudgetId()));
        }

        Expends expends = Expends.builder()
                .expendsName(expendsVo.getExpendsName())
                .commentary(expendsVo.getCommentary())
                .amount(expendsVo.getAmount())
                .user(user)
                .budget(budget)
                .build();

        return expendsRepository.save(expends);
    }

    public Expends getExpendsById(Long expendsId) {
        return expendsRepository.findByExpendsId(expendsId);
    }

    public List<Expends> getExpendsByBudgetId(Long budgetId) {
        return expendsRepository.findByBudget_BudgetId(budgetId);
    }
}
