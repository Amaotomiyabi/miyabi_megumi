package com.megumi.util;

import com.megumi.util.exception.IllegalMessageType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;

import static com.megumi.dict.MessageType.*;

/**
 * 邮件发送工具类
 * <p>
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
public class EmailSender {

    private final JavaMailSender sender;
    private final String from;

    public EmailSender(JavaMailSender sender, String from) {
        this.sender = sender;
        this.from = from;
    }

    public void send(String to, String subject, String text) throws MessagingException {
        var msg = sender.createMimeMessage();
        var helper = new MimeMessageHelper(msg, false);
        helper.setFrom(from);
        helper.setTo(to);
        if (subject == null) {
            subject = "雨";
        }
        helper.setSubject(subject);
        helper.setText(text, true);
        sender.send(msg);
    }

    public void sendVerifyCode(String to, String type, String code) throws MessagingException, IllegalMessageType {
        var msgType = switch (type) {
            case MAIL_CHANGE_EMAIL_NEW -> "更换邮箱——新邮箱验证";
            case MAIL_CHANGE_EMAIL_OLD -> "更换邮箱——旧箱验证";
            case MAIL_SIGN_UP -> "注册";
            case MAIL_CHANGE_PWD -> "更改密码";
            case EMAIL_ADD -> "绑定邮箱";
            case EMAIL_REMOVE -> "解绑邮箱";
            default -> null;
        };
        if (msgType == null) {
            throw new IllegalMessageType("发送邮件失败，错误的信息类型");
        }
        var text = """
                           <h1 style="color:pink">""" +
                   msgType
                   +
                   """
                           </h1>
                           <p>     尊敬的用户，您的验证码为 <u>"""
                   + code
                   + """
                           </u> 如非本人操作，请忽略本消息。</p>""";
        send(to, null, text);
    }

    public void signUp(String to, String code) throws MessagingException {
        var text = """
                           <h1 style="color:pink">注册</h1>
                           <p>     尊敬的用户，您的验证码为 <u>"""
                   + code
                   + """
                           </u> 如非本人操作，请忽略本消息</p>""";
        send(to, null, text);
    }

    public void findPwd(String to, String code) throws MessagingException {
        var text = """
                           <h1 style="color:pink">找回密码</h1>
                           <p>     尊敬的用户，您的验证码为 <u>"""
                   + code
                   + """
                           </u> 如非本人操作，请忽略本消息</p>""";
        send(to, null, text);
    }

    public void changeEmail(String to, String code) throws MessagingException {
        var text = """
                           <h1 style="color:pink">更换邮箱</h1>
                           <p>     尊敬的用户，您的验证码为 <u>"""
                   + code
                   + """
                           </u> 如非本人操作，请忽略本消息</p>""";
        send(to, null, text);
    }

    public void validateAuth(String to, String code) throws MessagingException {
        var text = """
                           <h1 style="color:pink">验证</h1>
                           <p>     尊敬的用户，您的验证码为 <u>"""
                   + code
                   + """
                           </u> 如非本人操作，请忽略本消息</p>""";
        send(to, null, text);
    }


}
