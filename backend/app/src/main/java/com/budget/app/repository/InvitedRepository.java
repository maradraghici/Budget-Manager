package com.budget.app.repository;

import com.budget.app.model.Invited;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitedRepository extends JpaRepository<Invited, Long> {

    Invited findByInvitedId(Long invitedId);
    Invited findByPhoneNumber(String phoneNumber);
}
