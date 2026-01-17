package com.budget.app.controller;

import java.text.ParseException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.budget.app.services.InvitedService;
import com.budget.app.vo.InvitedAuthorizeResponseVo;
import com.budget.app.vo.InvitedRequestVo;
import com.budget.app.vo.InvitedResponseVo;
import com.budget.app.vo.InvitedTokenResponseVo;
import com.budget.app.vo.InvitedUpdatePhoneVo;
import com.budget.app.vo.InvitedVo;

@RestController
@RequestMapping("/")
public class InvitedController {

    private final InvitedService service;

    public InvitedController(InvitedService service) {
        this.service = service;
    }

    @GetMapping("/invited/{id}")
    public ResponseEntity<InvitedResponseVo> getById(@PathVariable Long id) {
        InvitedResponseVo irv = service.getById(id);
        if (irv != null) {
            return ResponseEntity.ok(irv);
        } else {
            return ResponseEntity.notFound().build();
        }
        
    }

    @PostMapping("/invited/create")
    public void create(@RequestBody InvitedVo vo) {
        service.create(vo);
    }

    @DeleteMapping("/invited/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invited/authenticate")
    @CrossOrigin
    public InvitedTokenResponseVo login(@RequestBody InvitedVo invitedRequestVo) {
        return service.validateInvitedPhoneAndGenerateToken(invitedRequestVo);
    }

    @PostMapping("/invited/authorize")
    @CrossOrigin
    public InvitedAuthorizeResponseVo authorize(@RequestBody InvitedRequestVo userRequestVo) throws ParseException {
        return service.authorizeV2(userRequestVo);
    }

    @PostMapping("/invited/updatePhone")
    @CrossOrigin
    public ResponseEntity<?> updatePhoneNumber(
            @RequestHeader("Authorization") String authorization,
            @RequestBody InvitedUpdatePhoneVo vo
    ) {
        service.updatePhoneNumber(authorization, vo);
        return ResponseEntity.ok(Map.of("message", "Phone number updated successfully"));
    }
}

