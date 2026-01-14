package com.budget.app.controller;

import com.budget.app.services.UserExpendsService;
import com.budget.app.vo.UserExpendsResponseVo;
import com.budget.app.vo.UserExpendsVo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserExpendsController {
    @Autowired
    private UserExpendsService userExpendsService;

    @GetMapping("/userexpends/user/{userId}")
    @CrossOrigin
    public ResponseEntity<List<UserExpendsResponseVo>> getUserExpendsByUserId(@PathVariable Long userId) {
        List<UserExpendsResponseVo> userExpends = userExpendsService.getUserExpendsByUserId(userId);
        if (userExpends != null && !userExpends.isEmpty()) {
            return ResponseEntity.ok(userExpends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userexpends/expends/{expendsId}")
    public ResponseEntity<List<UserExpendsResponseVo>> getUserExpendsByExpendsId(@PathVariable Long expendsId) {
        List<UserExpendsResponseVo> userExpends = userExpendsService.getUserExpendsByExpendsId(expendsId);
        if (userExpends != null && !userExpends.isEmpty()) {
            return ResponseEntity.ok(userExpends);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/userexpends/find/{userExpendsId}")
    public ResponseEntity<UserExpendsResponseVo> getUserExpendsById(@PathVariable Long userExpendsId) {
        UserExpendsResponseVo userExpends = userExpendsService.getUserExpendsById(userExpendsId);
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

    @DeleteMapping("userexpends/delete/{userExpendsId}")
    public void deleteUserExpends(@PathVariable Long userExpendsId ){
        userExpendsService.deleteUserExpends(userExpendsId);
    }
}
