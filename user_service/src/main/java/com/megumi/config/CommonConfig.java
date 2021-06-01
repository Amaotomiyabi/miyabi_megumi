package com.megumi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.megumi.common.FileUtil;
import com.megumi.common.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
@Configuration
public class CommonConfig {

    public static final String PHOTO_SAVE_PATH;

    static {
        PHOTO_SAVE_PATH = ResourceBundle.getBundle("server").getString("file.photo.path");
    }


    @Bean("idGenerator")
    public IdGenerator getSnowInstance() {
        return IdGenerator.getInstance();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean("fileUtil")
    public FileUtil getFileUtil() throws IOException {
        return new FileUtil(IdGenerator.getInstance(), PHOTO_SAVE_PATH);
    }

}
