package com.budget.app.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpendsResponseVo {
    private Long expendsId;
    private String expendsName;
    private String commentary;
    private Double amount;
    private UserVo paidBy;
    private BudgetSimpleVo budget;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
