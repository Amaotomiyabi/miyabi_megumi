package com.megumi.controller;

import com.megumi.common.StateCode;
import com.megumi.service.MsgService;
import com.megumi.util.exception.IllegalMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
public class MsgController {

    private final MsgService msgService;

    @Autowired
    public MsgController(MsgService msgService) {
        this.msgService = msgService;
    }

    @GetMapping("/mail/send")
    public String sendMsgToEmail(String email, String type) throws MessagingException, IllegalMessageType {
        msgService.sendCodeMail(email, type);
        return StateCode.TRUE;
    }

    @GetMapping("/phone/send")
    public String sendMsgToPhone(String phone, String type) {
        msgService.sendCodeSms(phone, type);
        return StateCode.TRUE;
    }

    @GetMapping("/validate")
    public String validateMsg(String code, String info, String type) {
        return msgService.validateMsg(code, info, type) ? StateCode.TRUE : StateCode.FALSE;
    }
}
