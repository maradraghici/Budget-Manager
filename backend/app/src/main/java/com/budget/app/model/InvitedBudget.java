package com.budget.app.model;

import lombok.*;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "invited_budget_table",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"invited_id", "budget_id"})
    }
)
public class InvitedBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invited_budget_id")
    private Long invitedBudgetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_id")
    private Invited invited;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;
}
