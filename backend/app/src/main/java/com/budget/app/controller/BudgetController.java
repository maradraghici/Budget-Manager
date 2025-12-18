package com.budget.app.controller;

import com.budget.app.model.Budget;
import com.budget.app.services.BudgetService;
import com.budget.app.vo.BudgetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/budget/{budgetId}")
    @CrossOrigin
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long budgetId) {
        Budget budget = budgetService.getBudgetById(budgetId);
        if (budget != null) {
            return ResponseEntity.ok(budget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/budget/create")
    public void createNewBudget(@RequestBody BudgetVo budget) {
        budgetService.createNewBudget(budget);
    }
    
    @DeleteMapping("/budget/delete/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
