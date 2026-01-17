package com.budget.app.repository;

import com.budget.app.model.UserLogin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {

    @Query("SELECT u FROM UserLogin u WHERE u.user.userName = ?1  and u.token = ?2")
    public UserLogin findByUserAndToken(String userName, String token);

    UserLogin findByUser_UserIdAndToken(Long userId, String token);

    UserLogin findByUser_UserId(Long userId);

}
