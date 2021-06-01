package com.megumi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megumi.aspect.annotation.RequireValid;
import com.megumi.common.ResultBody;
import com.megumi.common.StateCode;
import com.megumi.common.StringUtils;
import com.megumi.repository.UserRepo;
import com.megumi.service.MsgService;
import com.megumi.service.remote.RemoteMsgService;
import com.megumi.util.RequestHolderUtil;
import com.megumi.util.exception.EmailValidateError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.message.AuthException;

import static com.megumi.common.StateCode.FALSE;
import static com.megumi.common.StateCode.TRUE;
import static com.megumi.dict.MessageType.*;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
@Service
public class MsgServiceImpl implements MsgService {

    private final UserRepo userRepo;
    private final RemoteMsgService remoteMsgService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MsgServiceImpl(UserRepo userRepo, RemoteMsgService remoteMsgService, ObjectMapper objectMapper) {
        this.userRepo = userRepo;
        this.remoteMsgService = remoteMsgService;
        this.objectMapper = objectMapper;
    }

    @Override
    public String sendCodeMail(String email, String type) throws JsonProcessingException {
        var result = remoteMsgService.sendMsgToEmail(email, type);
        var resultBody = objectMapper.readValue(result, ResultBody.class);
        if (!resultBody.getCode().equals(StateCode.SUCCESS)) {
            throw new RuntimeException(resultBody.getMessage());
        } else {
            return resultBody.getData().equals(TRUE) ? TRUE : FALSE;
        }
    }

    @Override
    public String sendCodeMsg(String phone, String type) {
        if (!remoteMsgService.sendMsgToPhone(phone, type).equals(TRUE)) {
            throw new RuntimeException("发送邮件失败");
        }
        return TRUE;
    }

    @Override
    public boolean validateMsg(String code, String info, String type) throws JsonProcessingException {
        var result = remoteMsgService.validateCode(code, info, type);
        var resultBody = objectMapper.readValue(result, ResultBody.class);
        if (!resultBody.getCode().equals(StateCode.SUCCESS)) {
            throw new RuntimeException(resultBody.getMessage());
        } else {
            return resultBody.getData().equals(TRUE);
        }
    }

    @Override
    @RequireValid
    public String sendSignUpEmail(String email) throws JsonProcessingException, EmailValidateError {
        if (userRepo.countUserByEmail(email) == 1) {
            throw new EmailValidateError("该邮箱已绑定账号");
        }
        sendCodeMail(email, MAIL_SIGN_UP);
        return TRUE;
    }

    @Override
    public String sendSignUpPhone(String phone) {
        if (userRepo.countUserByPhone(phone) == 1) {
            return "该手机号已绑定账号";
        }
        sendCodeMsg(phone, PHONE_SIGN_UP);
        return TRUE;
    }

    @Override
    public String sendSignInPhone(String phone) {
        if (userRepo.countUserByPhone(phone) != 1) {
            return "该手机号未注册";
        }
        sendCodeMsg(phone, PHONE_SIGN_IN);
        return TRUE;
    }

    @Override
    public String sendSignInEmail(String phone) throws EmailValidateError {
        if (userRepo.countUserByEmail(phone) != 1) {
            throw new EmailValidateError("该邮箱未注册");
        }
        sendCodeMsg(phone, PHONE_SIGN_IN);
        return TRUE;
    }

    @Override
    public String sendChangeEmailCodeToOld() throws AuthException, JsonProcessingException, EmailValidateError {
        String email;
        if (!StringUtils.isValid(email = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow().getEmail())) {
            throw new EmailValidateError("该账号没有绑定邮箱");
        }
        sendCodeMail(email, MAIL_CHANGE_EMAIL_OLD);
        return TRUE;
    }

    @Override
    public String sendChangeEmailCodeToNew(String email) throws JsonProcessingException, EmailValidateError {
        if (userRepo.countUserByEmail(email) != 0) {
            throw new EmailValidateError("该邮箱已绑定其他账号");
        }
        sendCodeMail(email, MAIL_CHANGE_EMAIL_NEW);
        return TRUE;
    }

    @Override
    @RequireValid
    public String sendAddEmail(String email) throws AuthException, JsonProcessingException, EmailValidateError {
        if (userRepo.countUserByEmail(email) != 0) {
            throw new EmailValidateError("该邮箱已绑定其他账号");
        }
        if (StringUtils.isValid(userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow().getEmail())) {
            throw new EmailValidateError("该账号已有绑定邮箱");
        }
        sendCodeMail(email, EMAIL_ADD);
        return TRUE;
    }

    @Override
    public String sendRemoveEmail() throws AuthException, EmailValidateError, JsonProcessingException {
        String email;
        if (!StringUtils.isValid(email = userRepo.findById(RequestHolderUtil.getUserId()).orElseThrow().getEmail())) {
            throw new EmailValidateError("该账号没有绑定任何邮箱");
        }
        sendCodeMail(email, EMAIL_REMOVE);
        return TRUE;
    }

    @Override
    @RequireValid
    public String sendModifyPwd(String email) throws JsonProcessingException, EmailValidateError {
        if (userRepo.countUserByEmail(email) == 0) {
            throw new EmailValidateError("该邮箱未绑定任何账号");
        }
        sendCodeMail(email, MAIL_CHANGE_PWD);
        return TRUE;
    }


}
