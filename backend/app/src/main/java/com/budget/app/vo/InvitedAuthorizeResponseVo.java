package com.budget.app.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedAuthorizeResponseVo {
    private String invitedId;
    private boolean authorized;
}
