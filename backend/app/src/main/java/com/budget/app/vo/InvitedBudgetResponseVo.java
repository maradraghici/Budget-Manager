package com.budget.app.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedBudgetResponseVo {

    private Long invitedBudgetId;
    private InvitedResponseVo invited;
    private BudgetSimpleVo budget;
}