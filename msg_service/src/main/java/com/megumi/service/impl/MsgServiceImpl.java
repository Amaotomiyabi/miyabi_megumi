package com.megumi.service.impl;

import com.megumi.common.StringUtils;
import com.megumi.util.EmailSender;
import com.megumi.service.MsgService;
import com.megumi.util.VerificationCode;
import com.megumi.util.exception.IllegalMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static com.megumi.common.StateCode.TRUE;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
@Service
public class MsgServiceImpl implements MsgService {
    private final static int codeTimeOut;

    static {
        codeTimeOut = Integer.parseInt(ResourceBundle.getBundle("server").getString("codeTimeOut"));
    }

    private final RedisTemplate<Object, Object> redisTemplate;
    private final EmailSender emailSender;

    @Autowired
    public MsgServiceImpl(RedisTemplate<Object, Object> redisTemplate, EmailSender emailSender) {
        this.redisTemplate = redisTemplate;
        this.emailSender = emailSender;
    }

    @Override
    public String sendCodeMail(String email, String type) throws MessagingException, IllegalMessageType {
        var code = VerificationCode.getCode();
        emailSender.sendVerifyCode(email, type, code);
        redisTemplate.opsForValue().set(StringUtils.splicing(email, type), code, codeTimeOut, TimeUnit.MINUTES);
        return TRUE;
    }

    @Override
    public String sendCodeSms(String phone, String type) {
        return null;
    }

    @Override
    public boolean validateMsg(String code, String info, String type) {
        var redisId = StringUtils.splicing(info, type);
        var existCode = redisTemplate.opsForValue().get(redisId);
        return existCode != null && existCode.equals(code);
    }
}
