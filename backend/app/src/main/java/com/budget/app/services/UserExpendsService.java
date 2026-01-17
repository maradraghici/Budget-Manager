package com.budget.app.services;

import com.budget.app.model.User;
import com.budget.app.model.UserExpends;
import com.budget.app.repository.UserExpendsRepository;
import com.budget.app.model.Expends;
import com.budget.app.repository.ExpendsRepository;
import com.budget.app.repository.UserRepository;
import com.budget.app.vo.ExpendsSimpleVo;
import com.budget.app.vo.UserExpendsResponseVo;
import com.budget.app.vo.UserExpendsVo;
import com.budget.app.vo.UserVo;

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

    public List<UserExpendsResponseVo> getUserExpendsByUserId(Long userId) {
        return userExpendsRepository.findByUserId_UserId(userId)
                .stream()
                .map(this::toResponseVo)
                .toList();
    }

    public UserExpendsResponseVo getUserExpendsById(Long userExpendsId) {
        UserExpends uE = userExpendsRepository.findByUserExpendsId(userExpendsId);
        if (uE == null) {
            throw new IllegalArgumentException("UserExpends not found: " + userExpendsId);
        }
        return toResponseVo(uE);
    }

    public List<UserExpendsResponseVo> getUserExpendsByExpendsId(Long expendsId) {
        return userExpendsRepository.findByExpendsId_ExpendsId(expendsId)
                .stream()
                .map(this::toResponseVo)
                .toList();
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
                .userId(user)
                .expendsId(expends)
                .build();

        return userExpendsRepository.save(newUserExpends);
    }

    public void deleteUserExpends(Long userExpendsId){
        if (userExpendsRepository.existsById(userExpendsId)){
            userExpendsRepository.deleteById(userExpendsId);
        } else {
            throw new IllegalArgumentException("User expends link not found: " + userExpendsId);
        }
    }

    private UserExpendsResponseVo toResponseVo(UserExpends ue) {
        return UserExpendsResponseVo.builder()
                .userExpendsId(ue.getUserExpendsId())
                .user(
                        UserVo.builder()
                                .userId(ue.getUserId().getUserId())
                                .userName(ue.getUserId().getUserName())
                                .email(ue.getUserId().getEmail())
                                .phoneNumber(ue.getUserId().getPhoneNumber())
                                .build()
                )
                .expends(
                        ExpendsSimpleVo.builder()
                                .expendsId(ue.getExpendsId().getExpendsId())
                                .expendsName(ue.getExpendsId().getExpendsName())
                                .amount(ue.getExpendsId().getAmount())
                                .build()
                )
                .build();
    }
}
