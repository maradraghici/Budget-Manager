package com.budget.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expends_table")
public class Expends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expends_id", updatable = false, nullable = false)
    private Long expendsId;

    @Column(name = "expends_name")
    private String expendsName;

    @Column(name = "commentary")
    private String commentary;
    
    @Column(name = "amount")
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Column(name = "created_at")
    private LocalDateTime createdAt; 
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; 

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
