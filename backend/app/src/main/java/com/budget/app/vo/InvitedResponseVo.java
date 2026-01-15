package com.budget.app.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedResponseVo {
    private Long invitedId;
    private String invitedName;
    private String phoneNumber;
}