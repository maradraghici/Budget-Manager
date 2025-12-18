package com.budget.app.repository;

import com.budget.app.model.Budget;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long>{
    public Budget findByBudgetId(Long budgetId);
}
