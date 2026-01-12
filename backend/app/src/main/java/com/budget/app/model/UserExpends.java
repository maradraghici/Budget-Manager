package com.budget.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "user_expends_table",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "expends_id"})
    }
)
public class UserExpends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_expends_id", updatable = false, nullable = false)
    private Long userExpendsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expends_id")
    private Expends expendsId;
}
