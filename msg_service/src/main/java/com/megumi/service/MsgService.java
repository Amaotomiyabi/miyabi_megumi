package com.megumi.service;

import com.megumi.util.exception.IllegalMessageType;

import javax.mail.MessagingException;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
public interface MsgService {

    String sendCodeMail(String email, String type) throws MessagingException, IllegalMessageType;

    String sendCodeSms(String phone, String type);

    /**
     * @param code 验证码
     * @param info Email/Phone
     * @param type 验证码类型
     * @return 是否有效
     */
    boolean validateMsg(String code, String info, String type);
}
