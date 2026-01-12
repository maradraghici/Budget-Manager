package com.budget.app.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BudgetResponseVo {
    private Long budgetId;
    private String budgetName;
    private String commentary;
    private UserVo user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
