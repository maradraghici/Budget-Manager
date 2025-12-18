package com.budget.app.controller;

import com.budget.app.model.UserBudget;
import com.budget.app.services.UserBudgetService;
import com.budget.app.vo.UserBudgetVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class UserBudgetController {
    @Autowired
    private UserBudgetService userBudgetService;

    @GetMapping("/userbudget/user/{userId}")
    @CrossOrigin
    public ResponseEntity<List<UserBudget>> getUserBudgetByUserId(@PathVariable Long userId) {
        List<UserBudget> userBudget = userBudgetService.getUserBudgetByUserId(userId);
        if (userBudget != null && !userBudget.isEmpty()) {
            return ResponseEntity.ok(userBudget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userbudget/budget/{budgetId}")
    public ResponseEntity<List<UserBudget>> getUserBudgetByBudgetId(@PathVariable Long budgetId) {
        List<UserBudget> userBudget = userBudgetService.getUserBudgetByBudgetId(budgetId);
        if (userBudget != null && !userBudget.isEmpty()) {
            return ResponseEntity.ok(userBudget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userbudget/find/{userBudgetId}")
    public ResponseEntity<UserBudget> getUserBudgetById(@PathVariable Long userBudgetId) {
        UserBudget userBudget = userBudgetService.getUserBudgetById(userBudgetId);
        if (userBudget != null) {
            return ResponseEntity.ok(userBudget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

    @PostMapping("/userbudget/create")
    public void createUserBudget(@RequestBody UserBudgetVo userBudget) {
        userBudgetService.createUserBudget(userBudget);
    }

    @DeleteMapping("/userbudget/delete/{userBudgetId}")
    public void deleteUserBudget(@PathVariable Long userBudgetId) {
        userBudgetService.deleteUserBudget(userBudgetId);
    }
    
    
}
