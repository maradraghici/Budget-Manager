package com.budget.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.budget.app.services.InvitedBudgetService;
import com.budget.app.vo.InvitedBudgetResponseVo;
import com.budget.app.vo.InvitedBudgetVo;

@RestController
@RequestMapping("/")
public class InvitedBudgetController {

    private final InvitedBudgetService service;

    public InvitedBudgetController(InvitedBudgetService service) {
        this.service = service;
    }

    @GetMapping("/invitedbudget/invited/{invitedId}")
    public ResponseEntity<List<InvitedBudgetResponseVo>> getByInvited(@PathVariable Long invitedId) {
        List<InvitedBudgetResponseVo> ListIRV = service.getByInvited(invitedId);
        if (ListIRV != null) {
            return ResponseEntity.ok(ListIRV);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/invitedbudget/budget/{budgetId}")
    public ResponseEntity<List<InvitedBudgetResponseVo>> getByBudget(@PathVariable Long budgetId) {
        List<InvitedBudgetResponseVo> ListIRV = service.getByBudget(budgetId);
        if (ListIRV != null) {
            return ResponseEntity.ok(ListIRV);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/invitedbudget/create")
    public void create(@RequestBody InvitedBudgetVo vo) {
        service.create(vo);
    }
}

