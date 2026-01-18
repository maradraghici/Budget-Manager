package com.budget.app.services;

import com.budget.app.model.Expends;
import com.budget.app.model.User;
import com.budget.app.model.Budget;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.vo.BudgetSimpleVo;
import com.budget.app.vo.ExpendsResponseVo;
import com.budget.app.vo.ExpendsVo;
import com.budget.app.vo.UserVo;

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
        if (expendsVo.getAmount() == null){
            throw new IllegalArgumentException("Amount is required");
        }
        
        if (expendsVo.getExpendsName() == null || expendsVo.getExpendsName().isBlank()){
            throw new IllegalArgumentException("Expends name is required");
        }

        if (expendsVo.getBudgetId() == null){
            throw new IllegalArgumentException("Budget reference is required");
        }
        
        if (expendsVo.getPaidById() == null){
            throw new IllegalArgumentException("User reference is required");
        }

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

    public ExpendsResponseVo getExpendsById(Long expendsId) {
        Expends expends = expendsRepository.findByExpendsId(expendsId);

        if (expends == null) {
            throw new IllegalArgumentException("Expends not found: " + expendsId);
        }

        return toResponseVo(expends);
    }

    public List<ExpendsResponseVo> getExpendsByBudgetId(Long budgetId) {
        return expendsRepository.findByBudget_BudgetId(budgetId)
                .stream()
                .map(this::toResponseVo)
                .toList();
    }

    public void deleteExpends(Long expendsId) {
        if (expendsRepository.existsById(expendsId)) {
            expendsRepository.deleteById(expendsId);
        } else {
            throw new IllegalArgumentException("Expends not found: " + expendsId);
        }
    } 

    public void deleteExpendsByUserId(Long userId){
        List<Expends> e = expendsRepository.findByUser_UserId(userId);

        if (!e.isEmpty()) {
            expendsRepository.deleteAll(e);
        }
    }

    // =========================
    // 🔁 Mapper interne
    // =========================
    private ExpendsResponseVo toResponseVo(Expends expends) {

        return ExpendsResponseVo.builder()
                .expendsId(expends.getExpendsId())
                .expendsName(expends.getExpendsName())
                .commentary(expends.getCommentary())
                .amount(expends.getAmount())
                .createdAt(expends.getCreatedAt())
                .updatedAt(expends.getUpdatedAt())
                .paidBy(
                        UserVo.builder()
                                .userId(expends.getUser().getUserId())
                                .userName(expends.getUser().getUserName())
                                .email(expends.getUser().getEmail())
                                .phoneNumber(expends.getUser().getPhoneNumber())
                                .build()
                )
                .budget(
                        BudgetSimpleVo.builder()
                                .budgetId(expends.getBudget().getBudgetId())
                                .budgetName(expends.getBudget().getBudgetName())
                                .build()
                )
                .build();
    }
}
