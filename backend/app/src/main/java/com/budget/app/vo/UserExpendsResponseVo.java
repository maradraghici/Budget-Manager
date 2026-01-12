package com.budget.app.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserExpendsResponseVo {
    private Long userExpendsId;
    private UserVo user;
    private ExpendsSimpleVo expends;    
}
