package com.budget.app.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpendsVo {
    private String expendsName;
    private String commentary;
    private Double amount;
    private Long budgetId;
    private Long paidById;
}