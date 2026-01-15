package com.budget.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public List<InvitedBudgetResponseVo> getByInvited(@PathVariable Long invitedId) {
        return service.getByInvited(invitedId);
    }

    @GetMapping("/invitedbudget/budget/{budgetId}")
    public List<InvitedBudgetResponseVo> getByBudget(@PathVariable Long budgetId) {
        return service.getByBudget(budgetId);
    }

    @PostMapping("/invitedbudget/create")
    public void create(@RequestBody InvitedBudgetVo vo) {
        service.create(vo);
    }
}

