package com.budget.app.model;

import lombok.*;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invited_login")
public class InvitedLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invited_login_id")
    private Long invitedLoginId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_id")
    private Invited invited;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Column(name = "token_expire_time", nullable = false)
    private String tokenExpireTime;
}
