package com.budget.app.repository;

import com.budget.app.model.UserExpends;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExpendsRepository extends JpaRepository<UserExpends, Long>{
    public UserExpends findByUserExpendsId(Long userExpendsId);
    public List<UserExpends> findByExpendsId_ExpendsId(Long expendsId);
    public List<UserExpends> findByUserId_UserId(Long userId);
}
