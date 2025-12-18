package com.budget.app.services;

import com.budget.app.model.User;
import com.budget.app.model.UserExpends;
import com.budget.app.repository.UserExpendsRepository;
import com.budget.app.model.Expends;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.UserExpendsVo;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserExpendsService {
    private final UserExpendsRepository userExpendsRepository;
    private final ExpendsRepository expendsRepository;
    private final UserRepository userRepository;

    public UserExpendsService(UserExpendsRepository userExpendsRepository, ExpendsRepository expendsRepository, UserRepository userRepository) {
        this.userExpendsRepository = userExpendsRepository;
        this.expendsRepository = expendsRepository;
        this.userRepository = userRepository;
    }

    public List<UserExpends> getUserExpendsByUserId(Long userId) {
        return userExpendsRepository.findByUserId_UserId(userId);
    }

    public UserExpends getUserExpendsById(Long userExpendsId) {
        return userExpendsRepository.findByUserExpendsId(userExpendsId);
    }

    public List<UserExpends> getUserExpendsByExpendsId(Long expendsId) {
        return userExpendsRepository.findByExpendsId_ExpendsId(expendsId);
    }

    public UserExpends createUserExpends(UserExpendsVo userExpendsVo) {
        User user = null;
        if (userExpendsVo.getUserId() != null) {
            user = userRepository.findById(userExpendsVo.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found: " + userExpendsVo.getUserId()));
        }
        Expends expends = null;
        if (userExpendsVo.getExpendsId() != null) {
            expends = expendsRepository.findById(userExpendsVo.getExpendsId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Expends not found: " + userExpendsVo.getExpendsId()));
        }

        UserExpends newUserExpends = UserExpends.builder()
                .userId(user) // Set userId appropriately
                .expendsId(expends) // Set expendsId appropriately
                .build();

        return userExpendsRepository.save(newUserExpends);
    }
}
