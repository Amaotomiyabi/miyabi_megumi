package com.megumi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.megumi.aspect.annotation.RequireValid;
import com.megumi.util.exception.EmailValidateError;
import com.megumi.util.exception.UserNotFoundException;

import javax.security.auth.message.AuthException;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
public interface MsgService {


    String sendCodeMail(String email, String type) throws JsonProcessingException;

    String sendCodeMsg(String phone, String type);

    boolean validateMsg(String code, String info, String type) throws JsonProcessingException;

    String sendSignUpEmail(String email) throws UserNotFoundException, JsonProcessingException, EmailValidateError;

    String sendSignUpPhone(String phone);

    String sendSignInPhone(String phone) throws UserNotFoundException;

    String sendSignInEmail(String phone) throws EmailValidateError;

    String sendChangeEmailCodeToOld() throws AuthException, JsonProcessingException, EmailValidateError;

    String sendChangeEmailCodeToNew(String email) throws AuthException, JsonProcessingException, EmailValidateError;

    String sendAddEmail(String email) throws AuthException, JsonProcessingException, EmailValidateError;

    String sendRemoveEmail() throws AuthException, EmailValidateError, JsonProcessingException;

    String sendModifyPwd(String email) throws JsonProcessingException, EmailValidateError;
}
