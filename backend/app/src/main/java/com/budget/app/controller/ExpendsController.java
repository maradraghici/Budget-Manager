package com.budget.app.controller;

import com.budget.app.model.Expends;
import com.budget.app.services.ExpendsService;
import com.budget.app.vo.ExpendsVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
public class ExpendsController {
    @Autowired
    private ExpendsService expendsService;

    @GetMapping("/expends/{expendsId}")
    @CrossOrigin
    public ResponseEntity<Expends> getExpendsById(@PathVariable Long expendsId) {
        Expends expends = expendsService.getExpendsById(expendsId);
        if (expends != null) {
            return ResponseEntity.ok(expends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/expends/create")
    public void createNewExpends(@RequestBody ExpendsVo expends) {
        expendsService.createNewExpends(expends);
    }

    @GetMapping("/expends/budget/{budgetId}")
    public ResponseEntity<List<Expends>> getExpendsBudget(@PathVariable Long budgetId) {
        List<Expends> expends = expendsService.getExpendsByBudgetId(budgetId);
        return ResponseEntity.ok(expends);
    }
    
    
}
