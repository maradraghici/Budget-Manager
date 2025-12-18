package com.budget.app.repository;

import com.budget.app.model.Expends;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpendsRepository extends JpaRepository<Expends, Long>{
    public Expends findByExpendsId(Long expendsId);
    public List<Expends> findByBudget_BudgetId(Long budgetId);
}
