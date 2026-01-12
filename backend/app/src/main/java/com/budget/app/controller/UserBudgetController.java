package com.budget.app.controller;

import com.budget.app.services.UserBudgetService;
import com.budget.app.vo.UserBudgetResponseVo;
import com.budget.app.vo.UserBudgetVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserBudgetController {
    @Autowired
    private UserBudgetService userBudgetService;

    @GetMapping("/userbudget/user/{userId}")
    @CrossOrigin
    public ResponseEntity<List<UserBudgetResponseVo>> getUserBudgetByUserId(@PathVariable Long userId) {
        List<UserBudgetResponseVo> userBudget = userBudgetService.getUserBudgetByUserId(userId);
        if (userBudget != null && !userBudget.isEmpty()) {
            return ResponseEntity.ok(userBudget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userbudget/budget/{budgetId}")
    public ResponseEntity<List<UserBudgetResponseVo>> getUserBudgetByBudgetId(@PathVariable Long budgetId) {
        List<UserBudgetResponseVo> userBudget = userBudgetService.getUserBudgetByBudgetId(budgetId);
        if (userBudget != null && !userBudget.isEmpty()) {
            return ResponseEntity.ok(userBudget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userbudget/find/{userBudgetId}")
    public ResponseEntity<UserBudgetResponseVo> getUserBudgetById(@PathVariable Long userBudgetId) {
        UserBudgetResponseVo userBudget = userBudgetService.getUserBudgetById(userBudgetId);
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
