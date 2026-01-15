package com.budget.app.services;

import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.budget.app.model.Budget;
import com.budget.app.model.Invited;
import com.budget.app.model.InvitedBudget;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.InvitedBudgetRepository;
import com.budget.app.repository.InvitedRepository;
import com.budget.app.vo.BudgetSimpleVo;
import com.budget.app.vo.InvitedBudgetResponseVo;
import com.budget.app.vo.InvitedBudgetVo;
import com.budget.app.vo.InvitedResponseVo;
@Service
public class InvitedBudgetService {

    private final InvitedBudgetRepository repository;
    private final InvitedRepository invitedRepository;
    private final BudgetRepository budgetRepository;

    public InvitedBudgetService(
            InvitedBudgetRepository repository,
            InvitedRepository invitedRepository,
            BudgetRepository budgetRepository
    ) {
        this.repository = repository;
        this.invitedRepository = invitedRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<InvitedBudgetResponseVo> getByInvited(Long invitedId) {
        return repository.findByInvited_InvitedId(invitedId)
                .stream()
                .map(this::toVo)
                .toList();
    }

    public List<InvitedBudgetResponseVo> getByBudget(Long budgetId) {
        return repository.findByBudget_BudgetId(budgetId)
                .stream()
                .map(this::toVo)
                .toList();
    }

    public void create(InvitedBudgetVo vo) {

        if (repository.existsByInvited_InvitedIdAndBudget_BudgetId(
                vo.getInvitedId(), vo.getBudgetId())) {
            throw new IllegalStateException("Invited already linked to this budget");
        }

        Invited invited = invitedRepository.findById(vo.getInvitedId())
                .orElseThrow(() -> new IllegalArgumentException("Invited not found"));

        Budget budget = budgetRepository.findById(vo.getBudgetId())
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        repository.save(
                InvitedBudget.builder()
                        .invited(invited)
                        .budget(budget)
                        .build()
        );
    }

    private InvitedBudgetResponseVo toVo(InvitedBudget ib) {
        return InvitedBudgetResponseVo.builder()
                .invitedBudgetId(ib.getInvitedBudgetId())
                .invited(
                        InvitedResponseVo.builder()
                                .invitedId(ib.getInvited().getInvitedId())
                                .invitedName(ib.getInvited().getInvitedName())
                                .phoneNumber(ib.getInvited().getPhoneNumber())
                                .build()
                )
                .budget(
                        BudgetSimpleVo.builder()
                                .budgetId(ib.getBudget().getBudgetId())
                                .budgetName(ib.getBudget().getBudgetName())
                                .build()
                )
                .build();
    }
}

