package com.budget.app.services;

import com.budget.app.model.UserBudget;
import com.budget.app.model.Budget;
import com.budget.app.model.User;
import com.budget.app.repository.UserBudgetRepository;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.BudgetSimpleVo;
import com.budget.app.vo.UserBudgetResponseVo;
import com.budget.app.vo.UserBudgetVo;
import com.budget.app.vo.UserVo;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserBudgetService {
    private final UserBudgetRepository userBudgetRepository;
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public UserBudgetService(UserBudgetRepository userBudgetRepository, BudgetRepository budgetRepository, UserRepository userRepository) {
        this.userBudgetRepository = userBudgetRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    public List<UserBudgetResponseVo> getUserBudgetByUserId(Long userId) {
        return userBudgetRepository.findByUserId_UserId(userId)
                .stream()
                .map(this::toResponseVo)
                .toList();
    }

    public UserBudgetResponseVo getUserBudgetById(Long userBudgetId) {
        UserBudget ub = userBudgetRepository.findByUserBudgetId(userBudgetId);
        if (ub == null) {
            throw new IllegalArgumentException("UserBudget not found: " + userBudgetId);
        }
        return toResponseVo(ub);
    }

    public List<UserBudgetResponseVo> getUserBudgetByBudgetId(Long budgetId) {
        return userBudgetRepository.findByBudgetId_BudgetId(budgetId)
                .stream()
                .map(this::toResponseVo)
                .toList();
    }

    public UserBudget createUserBudget(UserBudgetVo userBudget) {
        User user = null;
        if (userBudget.getPhoneNumber() != null) {
            user = userRepository.findByPhoneNumber(userBudget.getPhoneNumber());
            if (user == null){
                throw new IllegalArgumentException("User not found: " + userBudget.getPhoneNumber());
            }
        }
        Budget budget = null;
        if (userBudget.getBudgetId() != null) {
            budget = budgetRepository.findById(userBudget.getBudgetId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Budget not found: " + userBudget.getBudgetId()));
        }

        UserBudget newUserBudget = UserBudget.builder()
                .userId(user)
                .budgetId(budget)
                .build();

        return userBudgetRepository.save(newUserBudget);
    }

    public void deleteUserBudget(Long userBudgetId) {
        if (userBudgetRepository.existsById(userBudgetId)){
            userBudgetRepository.deleteById(userBudgetId);
        } else {
            throw new IllegalArgumentException("User expends link not found: " + userBudgetId);
        }
        
    }

    public void deleteUserPhoneBudget(UserBudgetVo userBudget) {
        User user = userRepository.findByPhoneNumber(userBudget.getPhoneNumber());

        if (user == null) {
            throw new IllegalArgumentException("User not found : " + userBudget.getPhoneNumber());
        }


        UserBudget ub = userBudgetRepository.findByUserId_UserIdAndBudgetId_BudgetId(user.getUserId(),userBudget.getBudgetId());
        

        if (ub != null){
            userBudgetRepository.delete(ub);
        } else {
            throw new IllegalArgumentException("User expends link not found.");
        }
        
    }

    private UserBudgetResponseVo toResponseVo(UserBudget ub) {
        return UserBudgetResponseVo.builder()
                .userBudgetId(ub.getUserBudgetId())
                .user(
                        UserVo.builder()
                                .userId(ub.getUserId().getUserId())
                                .userName(ub.getUserId().getUserName())
                                .email(ub.getUserId().getEmail())
                                .phoneNumber(ub.getUserId().getPhoneNumber())
                                .build()
                )
                .budget(
                        BudgetSimpleVo.builder()
                                .budgetId(ub.getBudgetId().getBudgetId())
                                .budgetName(ub.getBudgetId().getBudgetName())
                                .commentary(ub.getBudgetId().getCommentary())
                                .build()
                )
                .build();
    }
}
