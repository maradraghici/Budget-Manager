package com.budget.app.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ExpendsSimpleVo {
    private Long expendsId;
    private String expendsName;
    private Double amount;
}
