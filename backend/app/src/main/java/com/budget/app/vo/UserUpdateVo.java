package com.budget.app.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateVo {
    private String username;
    private String phoneNumber;

    // sécurité password
    private String oldPassword;
    private String newPassword;
}
