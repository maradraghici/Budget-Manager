package com.budget.app.services;

import com.budget.app.model.Budget;
import com.budget.app.model.Expends;
import com.budget.app.model.User;
import com.budget.app.model.UserBudget;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserBudgetRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.BudgetResponseVo;
import com.budget.app.vo.BudgetVo;
import com.budget.app.vo.UserVo;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final UserBudgetRepository userBudgetRepository;
    private final ExpendsRepository expendsRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository, UserBudgetRepository userBudgetRepository, ExpendsRepository expendsRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.userBudgetRepository = userBudgetRepository;
        this.expendsRepository = expendsRepository;
    }

    public BudgetResponseVo getBudgetById(Long budgetId) {
        Budget budget = budgetRepository.findByBudgetId(budgetId);

        if (budget == null) {
            throw new IllegalArgumentException("Budget not found: " + budgetId);
        }

        return toResponseVo(budget);
    }

    public List<BudgetResponseVo> getBudgetByCreator(Long userId){
        List<Budget> budget = budgetRepository.findByUser_UserId(userId);
        return budget.stream()
            .map(this::toResponseVo)
            .toList();
    }

    public Budget createNewBudget(BudgetVo budgetVo) {
        User user = null;
        if (budgetVo.getUserId() != null) {
            user = userRepository.findById(budgetVo.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found: " + budgetVo.getUserId()));
        }

        if (budgetVo.getBudgetName() == null || budgetVo.getBudgetName().isBlank()){
            throw new IllegalArgumentException("Budget name is required");
        }

        Budget budget = Budget.builder()
                .budgetName(budgetVo.getBudgetName())
                .commentary(budgetVo.getCommentary())
                .user(user)
                .build();

        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long budgetId) {
        List<UserBudget> ub = userBudgetRepository.findByBudgetId_BudgetId(budgetId);

        if (!ub.isEmpty()) {
            userBudgetRepository.deleteAll(ub);
        }

        List<Expends> e = expendsRepository.findByBudget_BudgetId(budgetId);

        if (!e.isEmpty()) {
            expendsRepository.deleteAll(e);
        }

        if (budgetRepository.existsById(budgetId)) {
            budgetRepository.deleteById(budgetId);
        } else {
            throw new IllegalArgumentException("Budget not found: " + budgetId);
        }
    }

    public void deleteBudgetByUserId(Long userId) {
        List<UserBudget> ub = userBudgetRepository.findByUserId_UserId(userId);

        if (!ub.isEmpty()) {
            userBudgetRepository.deleteAll(ub);
        }

        List<Budget> b = budgetRepository.findByUser_UserId(userId);

        if (!b.isEmpty()) {
            budgetRepository.deleteAll(b);
        }
    }

    private BudgetResponseVo toResponseVo(Budget budget) {
        return BudgetResponseVo.builder()
            .budgetId(budget.getBudgetId())
            .budgetName(budget.getBudgetName())
            .commentary(budget.getCommentary())
            .createdAt(budget.getCreatedAt())
            .updatedAt(budget.getUpdatedAt())
            .user(
                    UserVo.builder()
                            .userId(budget.getUser().getUserId())
                            .userName(budget.getUser().getUserName())
                            .email(budget.getUser().getEmail())
                            .phoneNumber(budget.getUser().getPhoneNumber())
                            .build()
            )
            .build();
    }
}
