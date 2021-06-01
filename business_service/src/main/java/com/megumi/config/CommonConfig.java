package com.megumi.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.megumi.common.FileUtil;
import com.megumi.common.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * 2021/2/23
 *
 * @author miyabi
 * @since 1.0
 */
@Configuration
public class CommonConfig {

    public static final String FILE_SAVE_PATH;

    public static final String IMG_SAVE_PATH;

    public static final String IMG_SMALL_SAVE_PATH;

    public static final String PHOTO_SAVE_PATH;

    static {
        PHOTO_SAVE_PATH = ResourceBundle.getBundle("server").getString("file.photo.path");
        FILE_SAVE_PATH = ResourceBundle.getBundle("server").getString("file.path");
        IMG_SAVE_PATH = ResourceBundle.getBundle("server").getString("file.img.path");
        IMG_SMALL_SAVE_PATH = ResourceBundle.getBundle("server").getString("file.img.small.path");
    }

    @Value("${spring.jackson.date-format}")
    private String pattern;

    @Bean("idGenerator")
    public IdGenerator getSnowInstance() {
        return IdGenerator.getInstance();
    }

    @Bean("fileUtil")
    public FileUtil getFileUtil() throws IOException {
        return new FileUtil(IdGenerator.getInstance(), FILE_SAVE_PATH);
    }

    @Bean
    public LocalDateTimeSerializer localDateTimeDeserializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeDeserializer());
    }


}
