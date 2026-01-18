package com.budget.app.repository;

import com.budget.app.model.UserBudget;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBudgetRepository extends JpaRepository<UserBudget, Long>{
    public List<UserBudget> findByUserId_UserId(Long userId);
    public UserBudget findByUserBudgetId(Long userBudgetId);
    public List<UserBudget> findByBudgetId_BudgetId(Long budgetId);
    public UserBudget findByUserId_UserIdAndBudgetId_BudgetId(
            Long userId,
            Long budgetId
    );
}
