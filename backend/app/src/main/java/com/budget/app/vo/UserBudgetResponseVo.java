package com.budget.app.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserBudgetResponseVo {
    private Long userBudgetId;
    private UserVo user;
    private BudgetSimpleVo budget;
}
