package com.budget.app.repository;

import com.budget.app.model.InvitedLogin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitedLoginRepository extends JpaRepository<InvitedLogin, Long> {

    @Query("SELECT i FROM InvitedLogin i WHERE i.invited.invitedName = ?1  and i.token = ?2")
    public InvitedLogin findByInvitedAndToken(String invitedName, String token);
}

