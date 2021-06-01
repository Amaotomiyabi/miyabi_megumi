package com.megumi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.megumi.service.MsgService;
import com.megumi.util.exception.EmailValidateError;
import com.megumi.util.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("/msg")
public class MsgController {

    private final MsgService msgService;

    @Autowired
    public MsgController(MsgService msgService) {
        this.msgService = msgService;
    }

    @PostMapping("/mail/register")
    public String sendMailSignUpCode(@RequestParam String email) throws UserNotFoundException, JsonProcessingException, EmailValidateError {
        return msgService.sendSignUpEmail(email);
    }

    @PostMapping("/mail/change/old")
    public String sendEmailChangeOldCode() throws AuthException, JsonProcessingException, EmailValidateError {
        return msgService.sendChangeEmailCodeToOld();
    }

    @PostMapping("/mail/add/new")
    public String sendEmailAddCode(@RequestParam String email) throws AuthException, JsonProcessingException, EmailValidateError {
        return msgService.sendAddEmail(email);
    }

    @PostMapping("/mail/remove")
    public String sendEmailRemoveCode() throws EmailValidateError, AuthException, JsonProcessingException {
        return msgService.sendRemoveEmail();
    }

    @PostMapping("/mail/pwd/change")
    public String sendEmailPwdChange(@RequestParam String email) throws JsonProcessingException, EmailValidateError {
        return msgService.sendModifyPwd(email);
    }
}
