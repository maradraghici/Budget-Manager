package com.budget.app.controller;

import com.budget.app.model.UserExpends;
import com.budget.app.services.UserExpendsService;
import com.budget.app.vo.UserExpendsVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class UserExpendsController {
    @Autowired
    private UserExpendsService userExpendsService;

    @GetMapping("/userexpends/user/{userId}")
    @CrossOrigin
    public ResponseEntity<List<UserExpends>> getUserExpendsByUserId(@PathVariable Long userId) {
        List<UserExpends> userExpends = userExpendsService.getUserExpendsByUserId(userId);
        if (userExpends != null && !userExpends.isEmpty()) {
            return ResponseEntity.ok(userExpends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userexpends/expends/{expendsId}")
    public ResponseEntity<List<UserExpends>> getUserExpendsByExpendsId(@PathVariable Long expendsId) {
        List<UserExpends> userExpends = userExpendsService.getUserExpendsByExpendsId(expendsId);
        if (userExpends != null && !userExpends.isEmpty()) {
            return ResponseEntity.ok(userExpends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userexpends/find/{userExpendsId}")
    public ResponseEntity<UserExpends> getUserExpendsById(@PathVariable Long userExpendsId) {
        UserExpends userExpends = userExpendsService.getUserExpendsById(userExpendsId);
        if (userExpends != null) {
            return ResponseEntity.ok(userExpends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/userexpends/create")
    public void createUserExpends(@RequestBody UserExpendsVo userExpends) {
        userExpendsService.createUserExpends(userExpends);
    }
}
