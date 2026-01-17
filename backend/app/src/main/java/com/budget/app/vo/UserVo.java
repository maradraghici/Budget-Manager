package com.budget.app.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserVo {
    private Long userId;
    private String userName;
    private String phoneNumber;
    private String email;
}
