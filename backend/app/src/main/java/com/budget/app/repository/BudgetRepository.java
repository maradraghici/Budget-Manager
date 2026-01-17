package com.budget.app.repository;

import com.budget.app.model.Budget;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>{
    public Budget findByBudgetId(Long budgetId);

    List<Budget> findByUser_UserId(Long userId);
}
