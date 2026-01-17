package com.budget.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.budget.app.model.InvitedBudget;

@Repository
public interface InvitedBudgetRepository extends JpaRepository<InvitedBudget, Long> {

    InvitedBudget findByInvitedBudgetId(Long id);

    List<InvitedBudget> findByInvited_InvitedId(Long invitedId);

    List<InvitedBudget> findByBudget_BudgetId(Long budgetId);

    boolean existsByInvited_InvitedIdAndBudget_BudgetId(Long invitedId, Long budgetId);
}

