package com.megumi.common;

import com.megumi.util.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class CommonConfig {

    @Value("${spring.mail.username}")
    private String from;

    @Bean("emailSender")
    @Autowired
    public EmailSender getEmailSender(JavaMailSender javaMailSender) {
        return new EmailSender(javaMailSender, from);
    }
}
